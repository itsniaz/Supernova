package com.wsec;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
public class PDFViewer extends AppCompatActivity {

    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        pdfView = findViewById(R.id.pdfViewer);

        String fileName = getIntent().getStringExtra("fileName");
        pdfView.fromAsset(fileName)
                .load();

    }
}
