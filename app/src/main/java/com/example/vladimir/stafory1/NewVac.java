package com.example.vladimir.stafory1;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.ArrayList;

class Vacancy {
    String date;
    String name;
    String comission;
    String offers;
    String href;
    boolean vac;
    Vacancy(String _date, String _name, String _comission, String _offers, String _href, boolean _vac){
        date = _date;
        name = _name;
        comission = _comission;
        offers = _offers;
        vac = _vac;
        href = _href;
    }
}

class VacancyAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater layoutInflater;
    AbstractList<Vacancy> objects;

    VacancyAdapter(Context context, ArrayList<Vacancy> vacancies){
        ctx = context;
        objects = vacancies;
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
            view = layoutInflater.inflate(R.layout.item, parent, false);
        }

        Vacancy v = getVacancy(position);

        ((TextView) view.findViewById(R.id.tvDescr)).setText(v.name);
        ((TextView) view.findViewById(R.id.tvPrice)).setText(v.date);
        ((TextView) view.findViewById(R.id.ivImage)).setText(v.offers);

        return view;
    }

    Vacancy getVacancy(int position) {
        return ((Vacancy) getItem(position));
    }

    ArrayList<Vacancy> getBox() {
        ArrayList<Vacancy> box = new ArrayList<Vacancy>();
        for (Vacancy p : objects) {
            if (p.vac)
                box.add(p);
        }
        return box;
    }

}
public class NewVac extends Fragment {

    static ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
    VacancyAdapter vacancyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        long start = System.currentTimeMillis();
        long connect = System.currentTimeMillis();
        System.out.println(connect - start + " TIME CONNECT");
        long parse = System.currentTimeMillis();
        System.out.println(parse - connect + " TIME PARSE");
        Elements tr = StaforyConnection.html.getElementsByTag("tr");
        int i = 1;
        for (Element vac : tr) {
            Elements vac1 = vac.getElementsByTag("td");
            i++;
        }
        //создаем текствью для записи вакансий

        //создаем массив строк для записи данных вакансии
        final String[] vacancy = new String[i - 1];
        int j = 0;
        String sss = "";
        for (Element vac : tr) {
            Elements vac1 = vac.getElementsByTag("td");
            int a = 0;
            for (Element vac2 : vac1) {
                if (a == 2) {
                }
                if (a == 3) {
                    vacancy[2] = vac2.getElementsByTag("div").text() + "\n";
                }
                if (a == 4) {
                    vacancy[3] = vac2.getElementsByTag("div").text() + "\n";
                }
                if (a == 1) {
                    vacancy[1] = vac2.getElementsByTag("a").text() + "\n";
                    Element link = vac2.select("a").first();
                    String linkHref = link.attr("href");
                    vacancy[4] = linkHref;
                }
                if (a == 0) {
                    vacancy[0] = vac2.getElementsByTag("div").text() + "\n";
                }
                a++;
            }
            vacancies.add(new Vacancy(vacancy[0], vacancy[1], vacancy[2], vacancy[3], vacancy[4], false));
        }
        long vac = System.currentTimeMillis();
        System.out.println(vac - parse + " TIME CREATE VAC");
        final VacancyAdapter vacancyAdapter = new VacancyAdapter(getActivity().getApplicationContext(), vacancies);
        view = inflater.inflate(R.layout.activity_new_vac, container, false);
        ListView lvMain = (ListView) view.findViewById(R.id.lvMain);
        lvMain.setAdapter(vacancyAdapter);
        long list = System.currentTimeMillis();
        System.out.println(list - vac + " TIME CREATE LIST");
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.fragmentTransaction = getFragmentManager().beginTransaction();
                MainActivity.fragmentTransaction.addToBackStack(null);
                MainActivity.fragmentTransaction.replace(R.id.frameLayout, MainActivity.vacView);
                NewVacItem.positionvac = position;
                MainActivity.fragmentTransaction.commit();
            }
        });

        return view;


    }
}

class NewVacItem extends Fragment {

    static int positionvac = 0;
    static ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
    VacancyAdapter vacancyAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        long start = System.currentTimeMillis();
        Elements tr = StaforyConnection.html.getElementsByTag("tr");
            int i = 1;
            for (Element vac : tr) {
                Elements vac1 = vac.getElementsByTag("td");
                i++;
            }
            //создаем текствью для записи вакансий

            //создаем массив строк для записи данных вакансии
            final String[] vacancy = new String[i - 1];
            int j = 0;
            String sss = "";
            for (Element vac : tr) {
                Elements vac1 = vac.getElementsByTag("td");
                int a = 0;
                for (Element vac2 : vac1) {
                    if (a == 2) {
                    }
                    if (a == 3) {
                        vacancy[2] = vac2.getElementsByTag("div").text() + "\n";
                    }
                    if (a == 4) {
                        vacancy[3] = vac2.getElementsByTag("div").text() + "\n";
                    }
                    if (a == 1) {
                        vacancy[1] = vac2.getElementsByTag("a").text() + "\n";
                        Element link = vac2.select("a").first();
                        String linkHref = link.attr("href");
                        vacancy[4] = linkHref;
                    }
                    if (a == 0) {
                        vacancy[0] = vac2.getElementsByTag("div").text() + "\n";
                    }
                    a++;
                }
                vacancies.add(new Vacancy(vacancy[0], vacancy[1], vacancy[2], vacancy[3], vacancy[4], false));
            }
            long vac = System.currentTimeMillis();
            final VacancyAdapter vacancyAdapter = new VacancyAdapter(getActivity().getApplicationContext(), vacancies);
            view = inflater.inflate(R.layout.activity_new_vac, container, false);
            ListView lvMain = (ListView) view.findViewById(R.id.lvMain);
            lvMain.setAdapter(vacancyAdapter);
            long list = System.currentTimeMillis();
            System.out.println(list-vac + " TIME CREATE LIST");
            lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.fragmentTransaction = getFragmentManager().beginTransaction();
                    MainActivity.fragmentTransaction.addToBackStack(null);
                    MainActivity.fragmentTransaction.replace(R.id.frameLayout, MainActivity.vacView);
                    positionvac = position;
                    MainActivity.fragmentTransaction.commit();
                }
            });
        return view;
    }

}
class VacView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = null;
        ArrayList<Vacancy> vacancies = NewVac.vacancies;
        Vacancy hrefv = vacancies.get(NewVacItem.positionvac - 1);
        try {
            StaforyConnection staforyConnection = MainActivity.staforyConnection;
            Document html1 = Jsoup.parse(staforyConnection.getContentuser("https://stafory.com" + hrefv.href));
            Elements desc = html1.getElementsByClass("item-desc");
            Elements divs = desc.select("div");
            try {
                String description =
                        "График: " + divs.get(31).text() + "\n" + "\n" +
                                "Оклад: " + divs.get(34).text() + "\n" + "\n" +
                                "Описание: " + divs.get(37).text() + "\n" + "\n" +
                                "Обязанности: " + divs.get(40).text() + "\n" + "\n" +
                                "Образование: " + divs.get(45).text() + "\n" + "\n" +
                                "Опыт работы: " + divs.get(48).text() + "\n" + "\n" +
                                "Навыки: " + divs.get(56).text() + "\n";

                //String [] vacancyFull = new String[];
                view = inflater.inflate(R.layout.vacancy, container, false);
                TextView textView = (TextView) view.findViewById(R.id.textView1);
                textView.setText(hrefv.name);
                TextView textView1 = (TextView) view.findViewById(R.id.textView2);
                textView1.setText(description);
            } catch (IndexOutOfBoundsException e) {
                assert view != null;
                TextView textView = (TextView) view.findViewById(R.id.textView1);
                textView.setText(hrefv.name);
                TextView textView1 = (TextView) view.findViewById(R.id.textView2);
                textView1.setText("Косячная вакансия!!!");
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
