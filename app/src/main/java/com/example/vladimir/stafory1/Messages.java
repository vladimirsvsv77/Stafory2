package com.example.vladimir.stafory1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class MessagesObject {
    String date;
    String text;
    String from;
    String href;
    boolean msgs;
    MessagesObject(String _date, String _text, String _from, String _href, boolean _msgs){
        date = _date;
        text = _text;
        from = _from;
        href = _href;
        msgs = _msgs;
    }
}

class MessagesAdapter  extends BaseAdapter {


    Context ctx;
    LayoutInflater layoutInflater;
    AbstractList<MessagesObject> objects;

    MessagesAdapter(Context context, ArrayList<MessagesObject> messagesObjects){
        ctx = context;
        objects = messagesObjects;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.messages_item, parent, false);
        }

        MessagesObject m = getMessages(position);

        //((TextView) view.findViewById(R.id.recImage)).setText(v.name);
        ((TextView) view.findViewById(R.id.mesFrom)).setText(m.from);
        ((TextView) view.findViewById(R.id.mesText)).setText(m.text);
        ((TextView) view.findViewById(R.id.mesDate)).setText(m.date);

        return view;
    }

    MessagesObject getMessages (int position) {
        return ((MessagesObject) getItem(position));
    }

    ArrayList<MessagesObject> getBox() {
        ArrayList<MessagesObject> box = new ArrayList<MessagesObject>();
        for (MessagesObject p : objects) {
            if (p.msgs)
                box.add(p);
        }
        return box;
    }
}

public class Messages extends Fragment {

        static int positionmesgs;
        static ArrayList<MessagesObject> messagesObjects = new ArrayList<MessagesObject>();
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = null;
            Document html1 = StaforyConnection.html1;
            Elements desc = html1.getElementsByTag("table");
            Elements td = desc.select("tr");
            for (int i = 0; i < td.size(); i++) {
                messagesObjects.add(new MessagesObject(
                        td.get(i).getElementsByClass("date").text(),
                        td.get(i).getElementsByClass("message").text(),
                        td.get(i).getElementsByClass("title").text(),
                        ("/messages/dialog/" + td.get(i).attr("data-opponent-id") + ".html"),
                        false
                ));
            }


            view = inflater.inflate(R.layout.activity_messages, container, false);
            MessagesAdapter messagesAdapter = new MessagesAdapter(getActivity().getApplicationContext(), messagesObjects);
            ListView messages = (ListView) view.findViewById(R.id.messages_list);
            messages.setAdapter(messagesAdapter);

            messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    positionmesgs = position;
                    MainActivity.fragmentTransaction = getFragmentManager().beginTransaction();
                    MainActivity.fragmentTransaction.addToBackStack(null);
                    MainActivity.fragmentTransaction.replace(R.id.frameLayout, MainActivity.messageView);
                    MainActivity.fragmentTransaction.commit();
                }
            });

            return view;
        }
    }

class MessagesAdapterView  extends BaseAdapter {


    Context ctx;
    LayoutInflater layoutInflater;
    AbstractList<MessagesObject> objects;

    MessagesAdapterView(Context context, ArrayList<MessagesObject> messagesObjects){
        ctx = context;
        objects = messagesObjects;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.message_dialog_item, parent, false);
        }

        MessagesObject m = getMessages(position);

        //((TextView) view.findViewById(R.id.recImage)).setText(v.name);
        ((TextView) view.findViewById(R.id.dialFrom)).setText(m.from);
        ((TextView) view.findViewById(R.id.dialText)).setText(m.text);
        ((TextView) view.findViewById(R.id.dialDate)).setText(m.date);

        return view;
    }

    MessagesObject getMessages (int position) {
        return ((MessagesObject) getItem(position));
    }

    ArrayList<MessagesObject> getBox() {
        ArrayList<MessagesObject> box = new ArrayList<MessagesObject>();
        for (MessagesObject p : objects) {
            if (p.msgs)
                box.add(p);
        }
        return box;
    }
}

class MessageView extends Fragment {
    Button send;
    EditText message;
    String messagetext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = null;
        ArrayList<MessagesObject> messages = new ArrayList<MessagesObject>();
        MessagesObject hrefm = Messages.messagesObjects.get(Messages.positionmesgs);
        try {
            Document html1 = Jsoup.parse(MainActivity.staforyConnection.getContentuser("https://stafory.com" + hrefm.href));
            Elements desc = html1.getElementsByClass("message-board-table");
            Elements td = desc.select("tr");
            System.out.println(td.size()+"SIZE!!");
            for (int i = 0; i < td.size(); i++) {
                messages.add(new MessagesObject(
                        td.get(i).getElementsByClass("date").text(),
                        td.get(i).getElementsByClass("message").text(),
                        td.get(i).getElementsByClass("title").text(),
                        "",
                        false
                ));
            }
            Collections.reverse(messages);
            for(MessagesObject m : messages){
                System.out.println(m.text);
            }

            view = inflater.inflate(R.layout.message, container, false);
            final MessagesAdapterView messagesAdapterView = new MessagesAdapterView(getActivity().getApplicationContext(), messages);
            ListView messagesview = (ListView) view.findViewById(R.id.messages_dialog);
            messagesview.setAdapter(messagesAdapterView);
            message = (EditText)view.findViewById(R.id.edit_message);
            send = (Button) view.findViewById(R.id.button_send);
            send.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessages send = new SendMessages();
                    messagetext = message.getText().toString();
                    send.message = messagetext;
                    send.execute();

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return view;
    }

    public class SendMessages extends AsyncTask<String, Void, String>  {
        String message;
        ArrayList<MessagesObject> messages = new ArrayList<MessagesObject>();
        MessagesObject hrefm = Messages.messagesObjects.get(Messages.positionmesgs);


        @Override
        protected String doInBackground(String... params) {
            try {
                MainActivity.staforyConnection.sendMesgs("https://stafory.com" + hrefm.href, message);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            } catch (KeyManagementException e) {
                e.printStackTrace();
                return null;
            }
            return "";
        }


        @Override
        protected void onPostExecute(String  succes) {
            int test;
        }
    }


}
