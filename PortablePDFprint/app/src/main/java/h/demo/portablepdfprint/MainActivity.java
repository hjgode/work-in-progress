package h.demo.portablepdfprint;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
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

import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.jpos.printer.CPCLPrinter;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {

    String TAG = "PortablePDFprint";
    TextView txtView1;
    TextView txtPreview;
    TextView txtStatus;
    ImageView imageView;
    File pdfFile = null;
    Bitmap pageImage=null;
    String pageText="This is PdfPortablePrint\nonly a demo text\nnothing special";
    Thread myThread;
    private Context context;
    boolean bCheckDisconnect=true;

    ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Vector<BluetoothDevice> remoteDevices;
    private ListView list;
    private BluetoothPort bluetoothPort;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
    private static final String fileName = dir + "//BTPrinter";
    private String lastConnAddr;
    private Thread hThread;

    private Button btnConnect;
    private Button btnSearch;
    Button btnPrint;

    EditText btAddrBox;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver disconnectReceiver;

    protected CPCLPrinter cpclPrinter;
    private int paperType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(cpclPrinter==null)
            cpclPrinter=new CPCLPrinter();

        context = this;

        //check and request permissions
        PermissionsClass permissions = new PermissionsClass(this);
        permissions.checkPermissions();

        //assign layout elements to vars
        setContentView(R.layout.activity_main);
        txtView1 = (TextView) findViewById(R.id.txtView1);
        txtPreview = (TextView) findViewById(R.id.txtPreview);
        txtStatus=(TextView)findViewById(R.id.txtStatus);
        imageView=(ImageView)findViewById(R.id.imageView);

        list = (ListView) findViewById(R.id.BtAddrListView);
        btnConnect = (Button) findViewById(R.id.ButtonConnectBT);
        btnSearch = (Button) findViewById(R.id.ButtonSearchBT);
        btnPrint=(Button)findViewById(R.id.btnPrint);
        btAddrBox=(EditText)findViewById(R.id.EditTextAddressBT);

        // Setting
        loadSettingFile();

        bluetoothSetup();
        //init PDFBox class
        PDFBoxResourceLoader.init(getApplicationContext());

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPrint();
            }
        });
        //setupBT();
        // Connect, Disconnect -- Button
        btnConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnConnect.setEnabled(false);
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
                btnConnect.setEnabled(true);
            }
        });
        // Search Button
        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mBluetoothAdapter.isDiscovering())
                {
                    clearBtDevData();
                    adapter.clear();
                    mBluetoothAdapter.startDiscovery();
                    showBTlist(true);
                }
                else
                {
                    mBluetoothAdapter.cancelDiscovery();
                    showBTlist(false);
                }
            }
        });
        // Bluetooth Device List
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        addPairedDevices();
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
                showBTlist(false);
            }
        });
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
                btnConnect.setEnabled(false);
                btAddrBox.setEnabled(false);
                btnSearch.setText("Stop Search");
            }
        };
        registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));

        searchFinish = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                btnConnect.setEnabled(true);
                btAddrBox.setEnabled(true);
                btnSearch.setText("Search");
            }
        };
        registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        disconnectReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Device is now connected
					Log.e(TAG, "Connected");
                    //doPrintText(); //does not work as connection not connected
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    //Device has disconnected
					Log.e(TAG, "Disconnected");
                    //DialogReconnectionOption();
                }
            }
        };
        registerReceiver(disconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        registerReceiver(disconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

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
                //strip text
//                renderPDF(pdfFile, true); //background
                renderPDF(pdfFile,false);

            } catch (Exception e) {
                // warn user about bad data here
                Log.d(TAG, "Exception: " + e.getMessage());
                finish();
//                return;
            }
        }
    }

    void doConnect(){
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
    public void renderPDF(final File aFile, final boolean doText) {
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
        hideImageView();
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
                parsedText=pdfStripper.getText(document);
                showParsedText(parsedText);
                pageText=parsedText;
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
                Log.d(TAG,status);
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
    void showBTlist(boolean bShow){
        if(bShow) {
            ((TextView) findViewById(R.id.txtPreview)).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.imageView)).setVisibility(View.GONE);
            ((ListView)findViewById(R.id.BtAddrListView)).setVisibility(View.VISIBLE);
        }else{
            ((TextView) findViewById(R.id.txtPreview)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageView)).setVisibility(View.GONE);
            ((ListView)findViewById(R.id.BtAddrListView)).setVisibility(View.GONE);
        }
    }

    // ####################### BLUETOOTH STUFF ################################
    @Override
    protected void onDestroy()
    {
        try
        {

            if(bluetoothPort.isConnected() == true)
            {
                unregisterReceiver(disconnectReceiver);
            }
            saveSettingFile();
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

    private void loadSettingFile()
    {
        int rin = 0;
        char [] buf = new char[128];
        try
        {
            FileReader fReader = new FileReader(fileName);
            rin = fReader.read(buf);
            if(rin > 0)
            {
                lastConnAddr = new String(buf,0,rin);
                btAddrBox.setText(lastConnAddr);
            }
            fReader.close();
        }
        catch (FileNotFoundException e)
        {
            Log.i(TAG, "Connection history not exists.");
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void saveSettingFile()
    {
        try
        {
            File tempDir = new File(dir);
            if(!tempDir.exists())
            {
                tempDir.mkdir();
            }
            FileWriter fWriter = new FileWriter(fileName);
            if(lastConnAddr != null)
                fWriter.write(lastConnAddr);
            fWriter.close();
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // Set up Bluetooth.
    private void bluetoothSetup()
    {
        // Initialize
        clearBtDevData();
        bluetoothPort = BluetoothPort.getInstance();
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
        btnConnect.setText("Connect");
        list.setEnabled(true);
        btAddrBox.setEnabled(true);
        btnSearch.setEnabled(true);
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
            updateStatus("doInBackground(BluetoothDevice...");
            Integer retVal = null;
            try
            {
                updateStatus("doInBackground() connect()...");
                bluetoothPort.connect(params[0]);
                updateStatus("doInBackground() connect() done");

                lastConnAddr = params[0].getAddress();
                retVal = Integer.valueOf(0);
//                doPrintText(); //makes the app crash
            }
            catch (IOException e)
            {
                Log.e(TAG, "doInBackground(BluetoothDevice"+e.getMessage());
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)	// Connection success.
            {
                //sewoo RequestHandler?!
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                // UI
                btnConnect.setText("Disconnect");
                list.setEnabled(false);
                btAddrBox.setEnabled(false);
                btnSearch.setEnabled(false);
                if(dialog.isShowing())
                    dialog.dismiss();
                Toast toast = Toast.makeText(MainActivity.this, "Bluetooth connected", Toast.LENGTH_SHORT);
                toast.show();
                //doPrintText();
                //renderPDF(pdfFile, false);
//                doPrint();//rashes app
/*
                registerReceiver(disconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                registerReceiver(disconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
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
    // Display the dialog when bluetooth disconnected.
    private void DialogReconnectionOption()
    {
        final String [] items			= new String [] {"Bluetooth printer"};
        android.app.AlertDialog.Builder builder		= new android.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.reconnect_msg));
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
            }
        }).setPositiveButton(getResources().getString(R.string.dev_conn_btn), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                try
                {
                    // Disconnect routine.
                    btDisconn();
                    btConn(mBluetoothAdapter.getRemoteDevice(btAddrBox.getText().toString()));
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
        }).setNegativeButton(getResources().getString(R.string.connect_cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // Cancel
                // Disconnect routine.
                btDisconn();
            }
        });
        builder.show();
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

    void doPrintText(){
        updateStatus("doPrintText() started...");
        if(pageText==null | pageText.length()==0) {
            Toast.makeText(this, "doPrint(): No Text found!",Toast.LENGTH_LONG);
            updateStatus("doPrint(): No Text found!");
            return;
        }
        if(!bluetoothPort.isConnected()){
            Toast.makeText(this, "doPrint(): No BT connection!",Toast.LENGTH_LONG);
            updateStatus("doPrint(): No BT connection!");

            return;
        }
        updateStatus("doPrintText() New CPCLprinter?...");
        //bluetooth should be already connected?!
        if(cpclPrinter==null) {
            cpclPrinter = new CPCLPrinter();
            Log.d(TAG, "doPrintText() new CPCLPrinter() created");
        }

        disableButtons();
        updateStatus("doPrintText() setup form");
        try {
            int cstatus, pstatus;
            cstatus = cpclPrinter.printerCheck(200);
            if(cstatus != 0)
            {
                Log.e(TAG,"printerCheck error");
                updateStatus("PrinterCheck error: "+cstatus);
            }
            cstatus = cpclPrinter.status();
            if(cstatus != 0)
            {
                Log.e(TAG,"Printer is error");
                updateStatus("Printer status error: "+cstatus);
            }
/*
            paperType = CPCLConst.LK_CPCL_CONTINUOUS;
            cpclPrinter.setForm(0, 200, 200, 500, 1);
            cpclPrinter.setMedia(paperType);
*/
            updateStatus("doPrintText() printing text...");
/*
            cpclPrinter.setMultiLine(45);
            cpclPrinter.multiLineText(0, 1, 0, 10, 10);
            cpclPrinter.multiLineData(pageText);
            cpclPrinter.resetMultiLine();
*/
            String testString="! 0 200 200 210 1\n" +
                    "TEXT 4 0 30 40 Hello World\n" +
                    "FORM\n" +
                    "PRINT";
            cpclPrinter.sendByte(testString.getBytes());
/*
            cpclPrinter.printCPCLText(0, 5, 2, 130, 140, "SEWOO TECH CO.,LTD.", 0);
            cpclPrinter.printCPCLText(0, 0, 3, 130, 210, "Dalim Plaza 304, 1027-20,", 0);
            cpclPrinter.printCPCLText(0, 0, 3, 130, 250, "Hogye-dong, Dongan-gu, Anyang-si,", 0);
            cpclPrinter.printCPCLText(0, 0, 3, 130, 290, "Gyeonggi-do, 431-848, Korea", 0);
            // Telephone
            cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 1, 130, 340, "TEL : 82-31-387-0101", 0);
            // Homepage
            cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 1, 310, 400, "www.miniprinter.com", 0);
            cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 1, 1, 310, 470, "<-- Check This.", 0);
            updateStatus("doPrintText() printing form");
            cpclPrinter.printForm();
*/
        }catch (Exception ex){

        }
        enableButtons();
        updateStatus("doPrintText() READY");
    }

    void doPrint(){
        int count=1;
        if(pageImage==null) {
            Toast.makeText(this, "doPrint(): No Image found!",Toast.LENGTH_LONG);
            updateStatus("doPrint(): No Image found!");
            return;
        }
        if(!bluetoothPort.isConnected()){
            Toast.makeText(this, "doPrint(): No BT connection!",Toast.LENGTH_LONG);
            updateStatus("doPrint(): No BT connection!");
            return;
        }

        //bluetooth should be already connected?!
        if(cpclPrinter==null)
            cpclPrinter=new CPCLPrinter();

        disableButtons();

        paperType = CPCLConst.LK_CPCL_CONTINUOUS;
        int lblHeigth=pageImage.getHeight()+10;
        cpclPrinter.setForm(0, 200, 200, lblHeigth, count);
        cpclPrinter.setMedia(paperType);
        try {
            int cstatus, pstatus;
            cstatus = cpclPrinter.printerCheck();
            if(cstatus != 0)
            {
                Log.e(TAG,"printerCheck error");
                updateStatus("PrinterCheck error: "+cstatus);
                return;
            }
            cstatus = cpclPrinter.status();
            if(cstatus != 0)
            {
                Log.e(TAG,"Printer is error");
                updateStatus("Printer status error: "+cstatus);
                return;
            }

            cpclPrinter.printBitmap(pageImage,1,1);
            cpclPrinter.printForm();
        }catch (Exception ex){

        }
        enableButtons();
    }
    void disableButtons(){
        btnConnect.setEnabled(false);
        btnSearch.setEnabled(false);
    }
    void enableButtons(){
        btnConnect.setEnabled(true);
        btnSearch.setEnabled(true);
    }

}
