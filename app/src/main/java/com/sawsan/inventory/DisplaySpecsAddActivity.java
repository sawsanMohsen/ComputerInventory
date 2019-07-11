package com.sawsan.inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sawsan.inventory.data.model.Computer;
import com.sawsan.inventory.remote.service.ApiService;
import com.sawsan.inventory.remote.service.ApiUtils;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DisplaySpecsAddActivity extends AppCompatActivity {
    private EditText processorNew;
    private EditText nameNew;
    private EditText brandNew;
    private EditText screenNew;
    private EditText modelNew;
    private EditText hddNew;
    private EditText vgaNew;
    private EditText ramNew;
    private Button btn_saveNew;
    private TextView tv_response;


    private ApiService apiService = ApiUtils.getAPIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_display_specs_add);

        nameNew = (EditText) findViewById(R.id.Name);
        processorNew = (EditText) findViewById(R.id.Processorf);
        ramNew = (EditText) findViewById(R.id.Memory);
        hddNew = (EditText) findViewById(R.id.Hdd);
        vgaNew = (EditText) findViewById(R.id.Vga);
        brandNew = (EditText) findViewById(R.id.Brand);
        modelNew = (EditText) findViewById(R.id.Model);
        screenNew= (EditText) findViewById(R.id.Screen);
        btn_saveNew = (Button) findViewById(R.id.Save);
        tv_response = (TextView) findViewById(R.id.tv_response);

        btn_saveNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Computer computer = new Computer();

                computer.setName(nameNew.getText().toString().trim());
                computer.setProcessor(processorNew.getText().toString().trim());
                computer.setRam(ramNew.getText().toString().trim());
                computer.setHdd(hddNew.getText().toString().trim());
                computer.setVga(vgaNew.getText().toString().trim());
                computer.setBrand(brandNew.getText().toString().trim());
                computer.setModel(modelNew.getText().toString().trim());
                computer.setScreen(screenNew.getText().toString().trim());
                push(computer);
            }
        });


    }


    private void push(Computer computer) {

        // RxJava
        apiService.insertComputer(computer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Computer>() {

                    CompositeDisposable singleInsertCompositeDisposable = new CompositeDisposable();

                    @Override
                    public void onSubscribe(Disposable d) {
                        singleInsertCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Computer computer) {
                        showResponse("New fields inserted: " + computer.toString());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                });
    }


    public void showResponse(String response) {
        if(tv_response.getVisibility() == View.GONE) {
            tv_response.setVisibility(View.VISIBLE);
        }
        tv_response.setText(response);
    }
}
