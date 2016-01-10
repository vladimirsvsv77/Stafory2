package com.example.vladimir.stafory1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.ArrayList;
class RecrutAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater layoutInflater;
    AbstractList<Recrut> objects;

    RecrutAdapter(Context context, ArrayList<Recrut> recruts){
        ctx = context;
        objects = recruts;
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
            view = layoutInflater.inflate(R.layout.recrut_item, parent, false);
        }

        Recrut r = getRecrut(position);

        //((ImageView) view.findViewById(R.id.recImage)).setImageBitmap(r.image);
        ((TextView) view.findViewById(R.id.recName)).setText(r.name);
        ((TextView) view.findViewById(R.id.recCity)).setText(r.city);
        ((TextView) view.findViewById(R.id.recReit)).setText(r.reiting);

        return view;
    }

    Recrut getRecrut(int position) {
        return ((Recrut) getItem(position));
    }

    ArrayList<Recrut> getBox() {
        ArrayList<Recrut> box = new ArrayList<Recrut>();
        for (Recrut p : objects) {
            if (p.rec)
                box.add(p);
        }
        return box;
    }

}
class Recrut {

    String name;
    String city;
    String reiting;
    String href;
    String image;
    String descr;
    String adress;
    String site;
    String exp;
    String phone;


    boolean rec;
    Recrut(String _name, String _city, String _reiting, String _href, boolean _rec){
        name = _name;
        city = _city;
        reiting = _reiting;
        rec = _rec;
        href = _href;
    }

}
public class Catalog extends Fragment {
    static ArrayList<Recrut> recruts = new ArrayList<Recrut>();
    static int positionrec;
    static String imagehref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        Document html1 = StaforyConnection.html2;
        Elements divs = html1.getElementsByClass("agent-item");
        for (int i = 0; i < divs.size(); i++) {
            recruts.add(new Recrut(
                    divs.get(i).getElementsByClass("name-block").text(),
                    divs.get(i).getElementsByClass("city-block").text(),
                    divs.get(i).getElementsByTag("markcount star-rating-readonly").text(),
                    divs.get(i).getElementsByTag("a").attr("href"),
                    false));
            recruts.get(i).image = "https://stafory.com" + divs.get(i).getElementsByTag("img").attr("src");
        }

        view = inflater.inflate(R.layout.activity_catalog, container, false);
        RecrutAdapter recrutAdapter = new RecrutAdapter(getActivity().getApplicationContext(), recruts);
        ListView catalog = (ListView) view.findViewById(R.id.catalog);
        catalog.setAdapter(recrutAdapter);

        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.fragmentTransaction = getFragmentManager().beginTransaction();
                MainActivity.fragmentTransaction.addToBackStack(null);
                MainActivity.fragmentTransaction.replace(R.id.frameLayout, MainActivity.recView);
                positionrec = position;
                MainActivity.fragmentTransaction.commit();
            }
        });

        return view;
    }
}




class RecView extends Fragment {

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = null;
        Recrut r = Catalog.recruts.get(Catalog.positionrec);
        try {
        StaforyConnection staforyConnection = MainActivity.staforyConnection;
        Document html1 = Jsoup.parse(staforyConnection.getContentuser("https://stafory.com" + r.href));

            Elements desc = html1.select("div").addClass("magents-profile-block");
            String rows = desc.get(2).text();

            view = inflater.inflate(R.layout.recerut_view_item, container, false);


            ((ImageView) view.findViewById(R.id.recImageView)).setImageBitmap(StaforyConnection.getBitmapFromURL(r.image));
            ((TextView) view.findViewById(R.id.recNameView)).setText(r.name);
            ((TextView) view.findViewById(R.id.recCityView)).setText(r.city);
            ((TextView) view.findViewById(R.id.recReitView)).setText(r.reiting);
            ((TextView) view.findViewById(R.id.recDescrView)).setText(desc.get(86).text());
            ((TextView) view.findViewById(R.id.recAdress)).setText(desc.get(68).text());
            ((TextView) view.findViewById(R.id.recSite)).setText(desc.get(74).text());
            ((TextView) view.findViewById(R.id.recExp)).setText(desc.get(65).text());
            ((TextView) view.findViewById(R.id.recPhone)).setText(desc.get(77).text());



        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return view;
    }

}
