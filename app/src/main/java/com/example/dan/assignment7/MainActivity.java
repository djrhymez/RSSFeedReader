package com.example.dan.assignment7;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    private ArticleAdapter articleAdapter;
    private ArrayList<Article> articles;
    private ListView listView;
    private String currentURL = "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Fhockey%2Fjets";
    private Intent settingsIntent;
    private SharedPreferences settingsPrefs;
    private String slateGrey = "#383838";
    private String white = "#ffffff";
    private View mainView;
    private View rootView;

    @Override
    protected void onResume() {
        super.onResume();
        String defaultFeed = settingsPrefs.getString("defaultFeed", currentURL);
        currentURL = defaultFeed;

        mainView = (View) findViewById(R.id.mainView);
        rootView = mainView.getRootView();
        String backgroundColor = settingsPrefs.getString("backgroundColor", white);

        rootView.setBackgroundColor(Color.parseColor(backgroundColor));

        RSSProcessing asyncRSSProcess = new RSSProcessing();
        asyncRSSProcess.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        RSSProcessing asyncRSSProcess = new RSSProcessing();
//        asyncRSSProcess.execute();

        settingsPrefs = getSharedPreferences("mainPrefs", 0);

        listView = (ListView) findViewById(R.id.lvFeed);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article singleArticle = articles.get(i);

                Intent intent = new Intent(MainActivity.this, singleArticleActivity.class);
                intent.putExtra("title", singleArticle.getTitle());
                intent.putExtra("date", singleArticle.getDatePublished());
                intent.putExtra("description", singleArticle.getDescription());
                intent.putExtra("link", singleArticle.getLink());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RSSProcessing asyncRSSProcess = new RSSProcessing();

        switch (item.getItemId()) {
            case R.id.bombersRSS:
                currentURL = "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Ffootball%2Fbombers";
                asyncRSSProcess.execute();
                return true;
            case R.id.jetsRSS:
                currentURL = "https://www.winnipegfreepress.com/rss/?path=%2Fsports%2Fhockey%2Fjets";
                asyncRSSProcess.execute();
                return true;
            case R.id.refresh:
                asyncRSSProcess.execute();
                return true;
            case R.id.settings:
                settingsIntent = new Intent(MainActivity.this, settingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ArticleAdapter extends ArrayAdapter<Article> {
        private ArrayList<Article> items;

        public ArticleAdapter(Context context, int textViewResourceId, ArrayList<Article> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            String textColor = settingsPrefs.getString("textColor", slateGrey);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.custom_item, null);
            }
            Article article = items.get(position);
            if (article != null) {
                TextView title = (TextView) v.findViewById(R.id.titleText);
                TextView datePublished = (TextView) v.findViewById(R.id.datePublishedText);
                if (title != null) {
                    title.setText(article.getTitle());
                    title.setTextColor(Color.parseColor(textColor));
                }
                if (datePublished != null) {
                    datePublished.setText(article.getDatePublished());
                    datePublished.setTextColor(Color.parseColor(textColor));
                }
            }
            return v;
        }
    }

    class FreePressHandler extends DefaultHandler {
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            articles = new ArrayList<>();
        }

        //ArrayLists for title and pubdate of the items in the feed
        private ArrayList<String> title;
        private ArrayList<String> pubDate;
        private ArrayList<String> description;
        private ArrayList<String> link;
        private StringBuilder stringBuilder;

        //flags to keep track of which elements we are in
        private boolean inTitle, inPubDate, inDescription, inLink;

        {
            title = new ArrayList<String>(10);
            pubDate =  new ArrayList<String>(10);
            description = new ArrayList<String>(10);
            link = new ArrayList<String>(10);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();

            //removes initial main title and description
            title.remove(0);
            description.remove(0);
            link.remove(0);

            int arraySize = title.size();

            for (int i = 0; i < arraySize; i++) {
                Article newArticle = new Article(title.get(i).toString(),pubDate.get(i).toString(), description.get(i).toString(), link.get(i).toString());
                articles.add(newArticle);
            }

            for(Article s:articles) {
                Log.d("Dan", s.getTitle().toString());
            }

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            stringBuilder = new StringBuilder();

            if (qName.equals("title")) {
                inTitle = true;
            } else if (qName.equals("pubDate")) {
                inPubDate = true;
            } else if (qName.equals("description")) {
                inDescription = true;
            } else if (qName.equals("link")) {
                inLink = true;
            }




        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if(inTitle) {
                title.add(stringBuilder.toString());
            } else if (inPubDate) {
                pubDate.add(stringBuilder.toString());
            } else if (inDescription) {
                description.add(stringBuilder.toString());
            }
            else if (inLink) {
                link.add(stringBuilder.toString());
            }

            if (qName.equals("title")) {
                inTitle = false;
            } else if (qName.equals("pubDate")) {
                inPubDate = false;
            } else if (qName.equals("description")) {
                inDescription = false;
            } else if (qName.equals("link")) {
                inLink = false;
            }

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            String s = new String(ch, start, length);
            stringBuilder.append(s);
        }
    }

    class RSSProcessing extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            articleAdapter = new ArticleAdapter(MainActivity.this, R.layout.custom_item,articles);
            listView.setAdapter(articleAdapter);
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            //create URL object to RSS file
            URL url = null;
            try {
                url = new URL(currentURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //create and open HTTP connection
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //create SAXParser to do all the hard work for us
            SAXParser saxParser = null;
            try {
                saxParser = SAXParserFactory.newInstance().newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            FreePressHandler freePressHandler = new FreePressHandler();

            //parse the RSS file using our custom handler
            try {
                saxParser.parse(connection.getInputStream(), freePressHandler);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class Article {
        private String title;
        private String datePublished;
        private String description;
        private String link;

        public Article(String title, String datePublished, String description, String link) {
            this.title = title;
            this.datePublished = datePublished;
            this.description = description;
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public String getDatePublished() {
            return datePublished;
        }

        public String getDescription() { return description; }

        public String getLink() { return link; }
    }
}
