package com.example.vladimir.stafory1;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
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


class CandidateObject {
    String name;
    String date;
    String city;
    String proffecian;
    String salary;
    String age;
    String href;
    String image;
    String scil;
    String edu;
    String vuz;
    String work;
    String timework;
    String position;
    String response;
    String phone;
    String email;


    boolean can;
    CandidateObject(String _name, String _date, String _city, String _proffecian, String _salary,
                    String _age, String _href, String _image, boolean _can){
        name = _name;
        date = _date;
        city = _city;
        proffecian = _proffecian;
        salary = _salary;
        age = _age;
        can = _can;
        href = _href;
        image = _image;
    }
}

class CandidateAdapter  extends BaseAdapter {

    Context ctx;
    LayoutInflater layoutInflater;
    AbstractList<CandidateObject> objects;

    CandidateAdapter(Context context, ArrayList<CandidateObject> candidateObjects){
        ctx = context;
        objects = candidateObjects;
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
            view = layoutInflater.inflate(R.layout.candidate_item, parent, false);
        }

        CandidateObject c = getCandidates(position);


        ((TextView) view.findViewById(R.id.canname)).setText(c.name);
        ((TextView) view.findViewById(R.id.canDate)).setText(c.date);
        ((TextView) view.findViewById(R.id.canCity)).setText(c.city);
        ((TextView) view.findViewById(R.id.canProff)).setText(c.proffecian);
        ((TextView) view.findViewById(R.id.canSalary)).setText(c.salary);
        ((TextView) view.findViewById(R.id.canAge)).setText(c.age);

        return view;
    }

    CandidateObject getCandidates (int position) {
        return ((CandidateObject) getItem(position));
    }

    ArrayList<CandidateObject> getBox() {
        ArrayList<CandidateObject> box = new ArrayList<CandidateObject>();
        for (CandidateObject p : objects) {
            if (p.can)
                box.add(p);
        }
        return box;
    }
}



public class Candidate extends Fragment {

        static ArrayList<CandidateObject> candidateObjects = new ArrayList<CandidateObject>();
        static int candidateposition;
        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle
        savedInstanceState){
            View view = null;
            Document html1 = StaforyConnection.html3;
            Element desc = html1.getElementsByClass("staff-table").get(1);
            Elements tr = desc.select("tr");
            for (int i = 0; i < tr.size(); i++) {
                Elements td = tr.get(i).getElementsByTag("td");
                try {
                    candidateObjects.add(new CandidateObject(
                            td.get(0).getElementsByTag("a").text(),
                            td.get(0).getElementsByTag("div").get(1).getElementsByTag("div").get(0).text(),
                            td.get(0).getElementsByTag("div").get(1).getElementsByTag("div").get(1).text(),
                            td.get(1).getElementsByTag("div").get(0).text(),
                            td.get(1).getElementsByTag("div").get(1).text(),
                            td.get(2).getElementsByTag("div").get(0).text(),
                            td.get(0).getElementsByTag("a").attr("href"),
                            ("https://stafory.com" +td.get(0).getElementsByTag("img").attr("src")),
                            false
                    ));
                }catch (IndexOutOfBoundsException e){
                    System.out.println("не удалось загрузить кандидата!");
                }

            }

            view = inflater.inflate(R.layout.activity_candidate, container, false);
            CandidateAdapter candidateAdapter = new CandidateAdapter(getActivity().getApplicationContext(), candidateObjects);
            ListView candidates = (ListView) view.findViewById(R.id.candidate_list);
            candidates.setAdapter(candidateAdapter);

            candidates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.fragmentTransaction = getFragmentManager().beginTransaction();
                    MainActivity.fragmentTransaction.addToBackStack(null);
                    MainActivity.fragmentTransaction.replace(R.id.frameLayout, MainActivity.canView);
                    candidateposition = position;
                    MainActivity.fragmentTransaction.commit();
                }
            });
            return view;
        }
    }

class CanView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = null;
        CandidateObject c = Candidate.candidateObjects.get(Candidate.candidateposition);
        try {
            StaforyConnection staforyConnection = MainActivity.staforyConnection;
            Document html1 = Jsoup.parse(staforyConnection.getContentuser("https://stafory.com" + c.href));
            Elements desc = html1.select("div").addClass("item-desc item-candidate");



            view = inflater.inflate(R.layout.candidate_view, container, false);

                try {
                ((ImageView) view.findViewById(R.id.canViewImage)).setImageBitmap(StaforyConnection.getBitmapFromURL(c.image));
                ((TextView) view.findViewById(R.id.canViewName)).setText(c.name);
                ((TextView) view.findViewById(R.id.canViewDate)).setText(c.date);
                ((TextView) view.findViewById(R.id.cabViewCity)).setText(c.city);
                ((TextView) view.findViewById(R.id.canViewProff)).setText(c.proffecian);
                ((TextView) view.findViewById(R.id.canViewSalaru)).setText(c.salary);
                ((TextView) view.findViewById(R.id.canViewAge)).setText(c.age);
                //parse
                ((TextView) view.findViewById(R.id.canViewScil)).setText(desc.get(62).text());
                ((TextView) view.findViewById(R.id.canViewEdu)).setText(desc.get(74).text());
                ((TextView) view.findViewById(R.id.canViewVuz)).setText(desc.get(77).text());
                ((TextView) view.findViewById(R.id.canViewWork)).setText(desc.get(124).text());
                ((TextView) view.findViewById(R.id.canViewTimework)).setText(desc.get(130).text());
                ((TextView) view.findViewById(R.id.canViewPosition)).setText(desc.get(127).text());
                ((TextView) view.findViewById(R.id.canViewResponse)).setText(desc.get(133).text());
                ((TextView) view.findViewById(R.id.canViewPhone)).setText(desc.get(138).text());
                ((TextView) view.findViewById(R.id.canViewEmail)).setText(desc.get(140).text());

            }catch (IndexOutOfBoundsException e){

            }
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