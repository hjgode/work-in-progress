package h.demo.portablepdfprint;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;
import com.tom_roush.pdfbox.*;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    String TAG = "PortablePDFprint";
    TextView txtView1;
    TextView txtPreview;
    TextView txtStatus;
    ImageView imageView;
    File pdfFile = null;
    Bitmap pageImage=null;
    Thread myThread;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        PermissionsClass permissions = new PermissionsClass(this);
        permissions.checkPermissions();

        setContentView(R.layout.activity_main);
        txtView1 = (TextView) findViewById(R.id.txtView1);
        txtPreview = (TextView) findViewById(R.id.txtPreview);
        txtStatus=(TextView)findViewById(R.id.txtStatus);
        imageView=(ImageView)findViewById(R.id.imageView);
        PDFBoxResourceLoader.init(getApplicationContext());

        setupBT();

        //have we launched by an Intent?
        Uri data = getIntent().getData();
        if (data != null) {
            getIntent().setData(null);
            try {
                pdfFile = new File(data.getPath());
                String fileName = pdfFile.getName();
                Toast.makeText(this, TAG + " " + fileName, Toast.LENGTH_LONG);
                Log.d(TAG, "data=" + data);
                txtView1.setText(fileName);
                theThread(pdfFile, true);
//                stripText();
            } catch (Exception e) {
                // warn user about bad data here
                Log.d(TAG, "Exception: " + e.getMessage());
                finish();
//                return;
            }
        }
    }

    public void theThread(final File aFile, final boolean doText) {
        if(doText) {
            hideImageView();
            new Thread(new Runnable() {
                File theFile = aFile;
                boolean bDoText = doText;

                @Override
                public void run() {
                    updateStatus("Extracting text from pdf started...");
                    stripText();
                }
            }).start();
        }else{
            hideTextView();
            renderFile(aFile);
        }
    }

    public boolean stripText() {
        boolean bRet = false;
        if (pdfFile == null || !pdfFile.exists()) {
            updateStatus("Mising PDF File. Stopped");
            return bRet;
        }
//        hideImageView();
//        disableButtons();
        String parsedText = null;
        PDDocument document = null;
        try {
            //document = PDDocument.load(assetManager.open("Hello.pdf"));
            document = PDDocument.load(pdfFile);
            bRet = true;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            updateStatus("Exception loading PDF File " + e.getMessage());
        }

        try {
            if (document!=null) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfStripper.setStartPage(0);
                pdfStripper.setEndPage(1);
                parsedText = "Parsed text: " + pdfStripper.getText(document);
                showParsedText(parsedText);
                updateStatus("READY");
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            updateStatus("Exception processing PDF File " + e.getMessage());
            bRet = false;
        } finally {
            try {
                if (document != null)
                    document.close();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
                updateStatus("Exception closing PDF File " + e.getMessage());
            }
            //enableButtons();
            return  bRet;
        }
    }

    /**
     * Loads an existing PDF and renders it to a Bitmap
     */
    public void renderFile(File theFile) {
        //disableButtons();
        // Render the page and save it to an image file
        Looper.myLooper();
        hideTextView();
        float myScale=3; //609 (printer) / 226 (pdf)
        try {
            updateStatus("Loading PDF File");
            // Load in an already created PDF
            PDDocument document = PDDocument.load(theFile);
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);

            updateStatus("Rendering PDF File...");
            // Render the image to an RGB Bitmap
            pageImage = renderer.renderImage(0, myScale, Bitmap.Config.RGB_565);
            updateStatus("Rendering PDF File DONE");
/*
			if(pageImage.getWidth() > printerWidthPx) //pageImage=226, printerWidthPx=609
                myScale=(float)(pageImage.getWidth() / printerWidthPx);
			else
                myScale=(float)(printerWidthPx / pageImage.getWidth());
			if (myScale!=1){ //create new scaled image for printer
			    pageImage=renderer.renderImage(0, 1, Bitmap.Config.RGB_565);
            }
*/

            // Save the render result to an image
/*            String path = root.getAbsolutePath() + "/Download/render.jpg";
            File renderFile = new File(path);
            FileOutputStream fileOut = new FileOutputStream(renderFile);
            pageImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
            fileOut.close();
            updateStatus("Successfully rendered image");
*/
            // Optional: display the render result on screen
            displayRenderedImage();
        } catch(Exception e) {
            e.printStackTrace();
        }
        //enableButtons();
    }

    void showParsedText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtPreview.setText(text);
            }
        });
        updateStatus("READY");
    }

    private void displayRenderedImage() {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageBitmap(pageImage);
                    }
                });
            }
        }.start();
    }

    void updateStatus(final String status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(status);
            }
        });
    }
    void hideTextView(){
        ((TextView)findViewById(R.id.txtPreview)).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.imageView)).setVisibility(View.VISIBLE);
    }
    void hideImageView(){
        ((TextView)findViewById(R.id.txtPreview)).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.imageView)).setVisibility(View.GONE);
    }

    // ####################### BLUETOOTH STUFF ################################
    @Override
    protected void onDestroy()
    {
        try
        {
/*
            if(bluetoothPort.isConnected() == true)
            {
                if(chkDisconnect.isChecked())
                {
                    unregisterReceiver(disconnectReceiver);
                }
            }
            saveSettingFile();
*/
            bluetoothPort.disconnect();
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        catch (InterruptedException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        if((hThread != null) && (hThread.isAlive()))
        {
            hThread.interrupt();
            hThread = null;
        }
        unregisterReceiver(searchFinish);
        unregisterReceiver(searchStart);
        unregisterReceiver(discoveryResult);
        super.onDestroy();
    }

    ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Vector<BluetoothDevice> remoteDevices;
    private ListView list;
    private BluetoothPort bluetoothPort;
    private static final int REQUEST_ENABLE_BT = 2;
    private String lastConnAddr;
    private Thread hThread;
    private Button connectButton;
    private Button searchButton;
    EditText btAddrBox;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver discoveryResult;

    void setupBT(){
        list = (ListView) findViewById(R.id.BtAddrListView);
        connectButton = (Button) findViewById(R.id.ButtonConnectBT);
        searchButton = (Button) findViewById(R.id.ButtonSearchBT);
        btAddrBox=(EditText)findViewById(R.id.EditTextAddressBT);
        bluetoothPort = BluetoothPort.getInstance();
        // Initialize
        clearBtDevData();
        // Connect, Disconnect -- Button
        connectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!bluetoothPort.isConnected()) // Connect routine.
                {
                    try
                    {
                        btConn(mBluetoothAdapter.getRemoteDevice(btAddrBox.getText().toString().toUpperCase()));
                    }
                    catch(IllegalArgumentException e)
                    {
                        // Bluetooth Address Format [OO:OO:OO:OO:OO:OO]
                        Log.e(TAG,e.getMessage(),e);
                        AlertView.showAlert(e.getMessage(), context);
                        return;
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG,e.getMessage(),e);
                        AlertView.showAlert(e.getMessage(), context);
                        return;
                    }
                }
                else // Disconnect routine.
                {
                    // Always run.
                    btDisconn();
                }
            }
        });
        // Search Button
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mBluetoothAdapter.isDiscovering())
                {
                    clearBtDevData();
                    adapter.clear();
                    mBluetoothAdapter.startDiscovery();
                }
                else
                {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });
        // Connect - click the List item.
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                BluetoothDevice btDev = remoteDevices.elementAt(arg2);
                try
                {
                    if(mBluetoothAdapter.isDiscovering())
                    {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    btAddrBox.setText(btDev.getAddress());
                    btConn(btDev);
                }
                catch (IOException e)
                {
                    AlertView.showAlert(e.getMessage(), context);
                    return;
                }
            }
        });


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            // Device does not support Bluetooth
            return;
        }
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Bluetooth Device List
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        addPairedDevices();

        // UI - Event Handler.
        // Search device, then add List.
        discoveryResult = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String key;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(remoteDevice != null)
                {
                    if(remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED)
                    {
                        key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"]";
                    }
                    else
                    {
                        key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"] [Paired]";
                    }
                    if(bluetoothPort.isValidAddress(remoteDevice.getAddress()))
                    {
                        remoteDevices.add(remoteDevice);
                        adapter.add(key);
                    }
                }
            }
        };
        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchStart = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                connectButton.setEnabled(false);
                btAddrBox.setEnabled(false);
                searchButton.setText("Stop Search");
            }
        };
        registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        searchFinish = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                connectButton.setEnabled(true);
                btAddrBox.setEnabled(true);
                searchButton.setText("Search");
            }
        };
        registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

    }
    // Bluetooth Connection method.
    private void btConn(final BluetoothDevice btDev) throws IOException
    {
        new connTask().execute(btDev);
    }
    // Bluetooth Disconnection method.
    private void btDisconn()
    {
        try
        {
            bluetoothPort.disconnect();
/*
            if(chkDisconnect.isChecked())
            {
                unregisterReceiver(disconnectReceiver);
            }
*/
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        if((hThread != null) && (hThread.isAlive()))
            hThread.interrupt();
        // UI
        connectButton.setText("Connect");
        list.setEnabled(true);
        btAddrBox.setEnabled(true);
        searchButton.setEnabled(true);
        Toast toast = Toast.makeText(context, "Bluetooth disconnected", Toast.LENGTH_SHORT);
        toast.show();
    }

    // Bluetooth Connection Task.
    class connTask extends AsyncTask<BluetoothDevice, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Bluetooth");
            dialog.setMessage("connecting");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params)
        {
            Integer retVal = null;
            try
            {
                bluetoothPort.connect(params[0]);

                lastConnAddr = params[0].getAddress();
                retVal = Integer.valueOf(0);
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)	// Connection success.
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                // UI
                connectButton.setText("Disconnect");
                list.setEnabled(false);
                btAddrBox.setEnabled(false);
                searchButton.setEnabled(false);
                if(dialog.isShowing())
                    dialog.dismiss();
                Toast toast = Toast.makeText(MainActivity.this, "Bluetooth connected", Toast.LENGTH_SHORT);
                toast.show();
/*
                if(chkDisconnect.isChecked())
                {
                    registerReceiver(disconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                    registerReceiver(disconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                }
*/
            }
            else	// Connection failed.
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                AlertView.showAlert("Bluetooth connection failed",
                        "Check device status or settings", MainActivity.this);
            }
            super.onPostExecute(result);
        }
    }

    // clear device data used list.
    private void clearBtDevData()
    {
        remoteDevices = new Vector<BluetoothDevice>();
    }
    // add paired device to list
    private void addPairedDevices()
    {
        BluetoothDevice pairedDevice;
        Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();
        while(iter.hasNext())
        {
            pairedDevice = iter.next();
            if(bluetoothPort.isValidAddress(pairedDevice.getAddress()))
            {
                remoteDevices.add(pairedDevice);
                adapter.add(pairedDevice.getName() +"\n["+pairedDevice.getAddress()+"] [Paired]");
            }
        }
    }
}
