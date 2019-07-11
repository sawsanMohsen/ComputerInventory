package com.sawsan.inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sawsan.inventory.data.model.Computer;
import com.sawsan.inventory.remote.service.ApiService;
import com.sawsan.inventory.remote.service.ApiUtils;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DisplaySpecsActivity extends AppCompatActivity {

    private TextView processor;
    private TextView name;
    private TextView brand;
    private TextView screen;
    private TextView model;
    private TextView hdd;
    private TextView vga;
    private TextView ram;
    private String computerId;

    // create an instance of the ApiService
    ApiService apiService = ApiUtils.getAPIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_specs);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getString("computerId") != null){
            computerId = bundle.getString("computerId");
        }
        Single<Computer> singleComputerObservable = apiService.getComputerData(computerId);
        singleComputerObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Computer>() {

                    CompositeDisposable singleCompositeDisposable = new CompositeDisposable();

                    @Override
                    public void onSubscribe(Disposable d) {

                        singleCompositeDisposable.add(d);
                        // we'll come back to this in a moment
                    }

                    @Override
                    public void onSuccess(Computer computer) {
                        // data is ready and we can update the UI
                        name.setText(computer.getName());
                        processor.setText(computer.getProcessor());
                        ram.setText(computer.getRam());
                        hdd.setText(computer.getHdd());
                        vga.setText(computer.getVga());
                        brand.setText(computer.getBrand());
                        model.setText(computer.getModel());
                        screen.setText(computer.getScreen());

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


        name = (TextView) findViewById(R.id.Name);
        processor = (TextView) findViewById(R.id.Processorf);
        ram = (TextView) findViewById(R.id.Memory);
        hdd = (TextView) findViewById(R.id.Hdd);
        vga = (TextView) findViewById(R.id.Vga);
        brand = (TextView) findViewById(R.id.Brand);
        model = (TextView) findViewById(R.id.Model);
        screen = (TextView) findViewById(R.id.Screen);

    }
}
