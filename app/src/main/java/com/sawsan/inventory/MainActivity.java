package com.sawsan.inventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sawsan.inventory.data.model.Computer;
import com.sawsan.inventory.remote.service.ApiService;
import com.sawsan.inventory.remote.service.ApiUtils;

import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
    private SwipeMenuListView listview;
    private TextView title;
    private Button btn_prev;
    private Button btn_next;
    private FloatingActionButton btn_add_computer;
    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<Computer> computerData = new ArrayList<Computer>();
    private SwipeMenuCreator creator;

    private int pageCount ;

    /**
     * Using this increment value we can move the listview items
     */
    public int increment = 0;

    /**
     * Here set the values, how the ListView to be display
     *
     * Be sure that you must set like this
     *
     * TOTAL_LIST_ITEMS > NUM_ITEMS_PAGE
     */

    public int TOTAL_LIST_ITEMS = 27;
    public int NUM_ITEMS_PAGE   = 8;

    // create an instance of the ApiService
    ApiService apiService = ApiUtils.getAPIService();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Single<Computer> computer = apiService.getComputerData("5d21c5d3e791a0326c64b757");
        Observable<Computer[]> computerObservable = apiService.getComputersData();
        //Observable computerObservable = Observable.fromArray(apiService.getComputersData());
        computerObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Computer[]>() {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Computer[] computers) {
                        TOTAL_LIST_ITEMS = computers.length;
                        for(int i=0; i<computers.length; i++){
                            computerData.add(computers[i]);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!compositeDisposable.isDisposed()) {
                            compositeDisposable.dispose();
                        }
                        MainActivity.super.onDestroy();
                    }

                    @Override
                    public void onComplete() {
                        setContentView(R.layout.activity_main);
                        listview = (SwipeMenuListView)findViewById(R.id.List);
                        btn_prev     = (Button)findViewById(R.id.prev);
                        btn_next     = (Button)findViewById(R.id.next);
                        title    = (TextView)findViewById(R.id.title);
                        btn_add_computer = (FloatingActionButton) findViewById(R.id.addComputer);
                        btn_add_computer.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                Intent addIntent = new Intent (MainActivity.this, DisplaySpecsAddActivity.class);
                                startActivity(addIntent);
                            }
                        });
                        btn_prev.setEnabled(false);
                        if(TOTAL_LIST_ITEMS <= NUM_ITEMS_PAGE){
                            btn_next.setEnabled(false);
                        }

                        int val = TOTAL_LIST_ITEMS%NUM_ITEMS_PAGE;
                        val = val==0?0:1;
                        pageCount = TOTAL_LIST_ITEMS/NUM_ITEMS_PAGE+val;
                        loadList(0);

                        btn_next.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                increment++;
                                loadList(increment);
                                CheckEnable();
                            }
                        });
                        btn_prev.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {

                                increment--;
                                loadList(increment);
                                CheckEnable();
                            }
                        });

                        if (!compositeDisposable.isDisposed()) {
                            compositeDisposable.dispose();
                        }
                        compositeDisposable.clear();
                        //MainActivity.super.onDestroy();
                    }
                });
    }

    private void CheckEnable() {
        btn_prev.setEnabled(true);
        btn_next.setEnabled(true);
        if(increment+1 == pageCount)
        {
            btn_next.setEnabled(false);
        }
        if(increment == 0)
        {
            btn_prev.setEnabled(false);
        }
    }

    /**
     * Method for loading data in listview
     * @param number
     */
    private void loadList(int number) {
        ArrayList<String> sort = new ArrayList<String>();

        final int num = number;

        final ArrayList<Computer> computers = new ArrayList<Computer>() {{
            int start = num * NUM_ITEMS_PAGE;
            for(int i=start;i<(start)+NUM_ITEMS_PAGE;i++){
                if(i<computerData.size()){
                    Computer sample = new Computer();
                    sample.setId(computerData.get(i).getId());
                    sample.setName(computerData.get(i).getName());
                    add(sample);
                }
                else {
                    break;
                }
            }
        }};

        final ArrayAdapter<Computer> adapter = new ArrayAdapter<Computer>(this, android.R.layout.simple_list_item_2, android.R.id.text1, computers) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(computers.get(position).getName());
                text2.setText(computers.get(position).getId());
                text2.setVisibility(view.GONE);
                return view;
            }
        };

        listview.setAdapter(adapter);
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(90);
                // set item title
                //openItem.setTitle("Update");
                //openItem.getBackground().setBounds(20,90,130,150);
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // set a icon
                openItem.setIcon(R.drawable.ic_update);

                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(90);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listview.setMenuCreator(creator);

        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        Intent update = new Intent(MainActivity.this, DisplaySpecsUpdateActivity.class);
                        update.putExtra("computerId", computers.get(position).getId());
                        startActivity(update);
                        break;
                    case 1:
                        // delete
                        //sd.remove(sd.getItem(position));
                        Single<String> singleStringObservable = apiService.deleteComputer(computers.get(position).getId());
                        final int pos = position;
                        singleStringObservable.subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new SingleObserver<String>() {

                                    CompositeDisposable singleCompositeDisposable = new CompositeDisposable();

                                    @Override
                                    public void onSubscribe(Disposable d) {

                                        singleCompositeDisposable.add(d);
                                        // we'll come back to this in a moment
                                    }

                                    @Override
                                    public void onSuccess(String response) {
                                        // data is ready and we can update the UI
                                        adapter.remove(adapter.getItem(pos));
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // oops, we best show some error message
                                        if (!singleCompositeDisposable.isDisposed()) {
                                            singleCompositeDisposable.dispose();
                                        }
                                        singleCompositeDisposable.clear();
                                    }
                                });

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open
                Intent openItem = new Intent(MainActivity.this, DisplaySpecsActivity.class);
                openItem.putExtra("computerId", computers.get(position).getId());
                startActivity(openItem);
            }
        });

    }

}
