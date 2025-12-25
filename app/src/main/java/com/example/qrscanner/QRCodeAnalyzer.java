package com.example.qrscanner;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.*;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QRCodeAnalyzer implements ImageAnalysis.Analyzer {

    public interface QRListener {
        void onQRCodeScanned(String result);
    }

    private final QRListener listener;
    private final BarcodeScanner scanner;

    public QRCodeAnalyzer(QRListener listener) {
        this.listener = listener;
        scanner = BarcodeScanning.getClient(
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
        );
    }

    @ExperimentalGetImage
    @Override
    public void analyze(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        if (barcode.getRawValue() != null) {
                            listener.onQRCodeScanned(barcode.getRawValue());
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }
}
