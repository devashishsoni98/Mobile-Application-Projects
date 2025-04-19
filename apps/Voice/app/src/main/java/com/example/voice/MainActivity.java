package com.example.voice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView textView;
    private Button startButton, translateButton;
    private EditText translatedText;
    private Spinner languageSpinner;
    private Translate translate;

    // **HARDCODED API KEY**
    private static final String API_KEY = "AIzaSyBtUc-7CB7L6djZXXU0imWA_-JIHc2XTSE";

    private final String[] languageCodes = {"es", "fr", "de", "hi"}; // Spanish, French, German, Hindi
    private final String[] languageNames = {"Spanish", "French", "German", "Hindi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        startButton = findViewById(R.id.startButton);
        translateButton = findViewById(R.id.translateButton);
        translatedText = findViewById(R.id.translatedText);
        languageSpinner = findViewById(R.id.languageSpinner);

        // Populate Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languageNames);
        languageSpinner.setAdapter(adapter);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechListener());

        startButton.setOnClickListener(v -> startSpeechRecognition());
        translateButton.setOnClickListener(v -> translateText());

        requestPermissions();
        initializeTranslateService();
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.startListening(intent);
    }

    private void translateText() {
        new Thread(() -> {
            try {
                String originalText = textView.getText().toString().trim();
                if (originalText.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "No text to translate", Toast.LENGTH_SHORT).show());
                    return;
                }

                String targetLanguage = languageCodes[languageSpinner.getSelectedItemPosition()];
                Translation translation = translate.translate(originalText,
                        Translate.TranslateOption.targetLanguage(targetLanguage),
                        Translate.TranslateOption.model("nmt"));

                runOnUiThread(() -> translatedText.setText(translation.getTranslatedText()));
            } catch (Exception e) {
                runOnUiThread(() -> translatedText.setText("Translation failed: " + e.getMessage()));
            }
        }).start();
    }

    private void initializeTranslateService() {
        new Thread(() -> {
            try {
                TranslateOptions translateOptions = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                translate = translateOptions.getService();
                Log.d("Translation", "Google Translate API Initialized Successfully!");
            } catch (Exception e) {
                Log.e("Translation", "Error initializing Google Translate API: " + e.getMessage());
            }
        }).start();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied! App cannot access microphone", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private class SpeechListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("Speech", "Ready for speech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("Speech", "Speech started");
        }

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {
            Log.d("Speech", "Speech ended");
        }

        @Override
        public void onError(int error) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Speech recognition error: " + error, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                textView.setText(matches.get(0));
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }
}
