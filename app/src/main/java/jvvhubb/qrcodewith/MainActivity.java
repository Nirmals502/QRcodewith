package jvvhubb.qrcodewith;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeException;
import com.datalogic.decode.DecodeResult;
import com.datalogic.decode.ReadListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static com.datalogic.decode.BarcodeManager.ACTION_START_DECODE;

public class MainActivity extends AppCompatActivity {
    Button BTN_SCAN_IMAGE;
    TextView TXT_result;
    BarcodeManager decoder = null;
    TextView mBarcodeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTN_SCAN_IMAGE = (Button) findViewById(R.id.BTN_SCN_IMAGE);
        TXT_result = (TextView) findViewById(R.id.TXT_RESULT);
        BTN_SCAN_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean result = Utility.checkPermission(MainActivity.this);
//
//                if (result) {
//                    selectImage_();
//
//                    //galleryIntent();
//
//
//                }
                try {
                    if (decoder == null) {
                        decoder = new BarcodeManager();
                        decoder.addReadListener(new ReadListener() {
                            @Override
                            public void onRead(DecodeResult decodeResult) {

                                TXT_result.setText(decodeResult.getText());
                            }


                        });
                    }

                } catch (DecodeException e) {

                    e.printStackTrace();
                }
            }
        });
    }

    // Interesting method
    public static String decodeQRImage(String path) {
        Bitmap bMap = BitmapFactory.decodeFile(path);
        String decoded = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            decoded = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return decoded;
    }

    private void selectImage_() {

        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {

                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);

                    String Result = decodeQRImage(picturePath);
                    TXT_result.setText(Result);
                    Toast.makeText(MainActivity.this, Result, Toast.LENGTH_LONG).show();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
