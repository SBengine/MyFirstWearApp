/*
 * ****************************************************************************
 * Copyright 2015-2016 @author Bruno Salvatore Belluccia - Google developer group Gela - on 22/10/15
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * more edu slides on SB engine ICT consulting http://www.sbengine.com
 *
 * ****************************************************************************
 */

package com.sbengine.myfirstwearapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class WearActivity extends Activity implements   GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener,
                                                        DataApi.DataListener,
                                                        MessageApi.MessageListener,
                                                        NodeApi.NodeListener
{
    private final String LOG_TAG="WearActivity";
    private GoogleApiClient mGoogleApiClient;
    private final String START_ACTIVITY_PATH="/path/startactivity";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        //dobbiamo aspettare che la grafica sia pronta per questo usiamo un OnLayoutInflatedListener
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub)
            {
                mTextView = (TextView) stub.findViewById(R.id.text);

                mTextView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
//                        //non creaiamo mai un dataItem diretto ma creaiamo una DataMap
//                        PutDataMapRequest dataMap = PutDataMapRequest.create("/path");//il path è il path della request
//                        //facciamo il put di un oggetto
//                        dataMap.getDataMap().putString("/itemID", "Hello Mobile! I'm an wearActivity in GDG devFest Mediterranean 2015");
//                        //creiamo la richiesta
//                        PutDataRequest request = dataMap.asPutDataRequest();
//
//                        //facciamo la richiesta
//                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
//
//                        mTextView.setText("Stringa inviata");//e messa in cache non sarà più inviata
//                        Log.i(LOG_TAG,"STRINGA INVIATA");

                        //PER INVIARE UN MESSAGGIO

                        new SendMessageTask().execute(START_ACTIVITY_PATH, null);
                    }
                });
                }
        });

//        GoogleApiClient è la classe che attualmente permette la connessione tra wear e moduli mobile.
//        La connessione è possibile grazie ai Google Play Services. In questo caso usiamo la Wearable.API
//        Usiamo un Builder per costruire un GoogleApiClient ed aggiungiamo dei listener come ConnectionCallbacks e OnConnectionFailedListener
//        L'ApiClient deve essere usato sia nella parte mobile che nella parte wear

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    //ConnectionCallbacks listeners

    @Override
    public void onConnected(Bundle bundle)
    {
        //aggiungiamo i listeners
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    //FINE ConnectionCallbacks listeners

    //OnConnectionFailedListener

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    //DATA LISTENER
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer)
    {
        for (DataEvent event : dataEventBuffer)
        {
            //quando viene eliminato un dataItem viene inviato un dataEvent di tipo DELETED
            if (event.getType() == DataEvent.TYPE_DELETED)
            {

            }
            //in tutti gli altri casi sia quando viene sincronizzato la prima volta sia quando viene modificato
            else if (event.getType() == DataEvent.TYPE_CHANGED)
            {
                //recuperiamo il dataItem
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                //recuperiamo la dataMap
                DataMap dataMap = dataMapItem.getDataMap();
                //estraiamo la stringa necessaria
                String s = dataMap.getString("/itemID");

                Toast toast = Toast.makeText(this, "Messaggio dal dispositivo: " + s, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    //MESSAGE LISTENER

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH))
        {
            //TODO qualcosa ad esempio fare partire un'activity
            //            Intent startIntent = new Intent(this, MainActivity.class);
            //            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //            startActivity(startIntent);

            Toast toast = Toast.makeText(this, "Messaggio dal dispositivo! faccio partire un'activity", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //NODE LISTENERS

    @Override
    public void onPeerConnected(Node node)
    {

    }

    @Override
    public void onPeerDisconnected(Node node)
    {

    }

    private Collection<String> getNodes()
    {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes())
        {
            results.add(node.getId());
        }

        return results;
    }

    private class SendMessageTask extends AsyncTask<Object, Void, Object>
    {
        @Override
        protected Void doInBackground(Object... args)
        {
            Collection<String> nodes = getNodes();
            for (String node : nodes)
            {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                        mGoogleApiClient, node, (String) args[0]/*PATH*/, (byte[]) args[1]/*payload byte array*/).await();

                if (result.getStatus().isSuccess())
                {
                    Log.i(LOG_TAG,"MESSAGGIO INVIATO CORRETTAMENTE");
                }
                else
                {
                    Log.e(LOG_TAG, "ERRORE: invio del messaggio fallito: " + result.getStatus());
                }

            }

            return null;
        }
    }
}
