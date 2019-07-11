package com.sawsan.inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sawsan.inventory.data.model.Computer;
import com.sawsan.inventory.remote.service.ApiService;
import com.sawsan.inventory.remote.service.ApiService;
import com.sawsan.inventory.remote.service.ApiUtils;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DisplaySpecsUpdateActivity extends AppCompatActivity {

    private EditText processor;
    private EditText name;
    private EditText brand;
    private EditText screen;
    private EditText model;
    private EditText hdd;
    private EditText vga;
    private EditText ram;
    private Button btn_save;
    private TextView tv_response;
    private TextView computer_primary_id;
    private String computerId;
    // create an instance of the ApiService
    ApiService apiService = ApiUtils.getAPIService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_specs_update);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("computerId") != null){
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
                        computer_primary_id.setText(computer.getId());

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


        name = (EditText) findViewById(R.id.Name);
        processor = (EditText) findViewById(R.id.Processorf);
        ram = (EditText) findViewById(R.id.Memory);
        hdd = (EditText) findViewById(R.id.Hdd);
        vga = (EditText) findViewById(R.id.Vga);
        brand = (EditText) findViewById(R.id.Brand);
        model = (EditText) findViewById(R.id.Model);
        screen = (EditText) findViewById(R.id.Screen);
        btn_save = (Button) findViewById(R.id.Save);
        tv_response = (TextView) findViewById(R.id.tv_response);
        computer_primary_id = (TextView) findViewById(R.id.computer_primary_id);



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Computer updatedComputer = new Computer();

                updatedComputer.setId(computer_primary_id.getText().toString().trim());
                updatedComputer.setName(name.getText().toString().trim());
                updatedComputer.setProcessor(processor.getText().toString().trim());
                updatedComputer.setRam(ram.getText().toString().trim());
                updatedComputer.setHdd(hdd.getText().toString().trim());
                updatedComputer.setVga(vga.getText().toString().trim());
                updatedComputer.setBrand(brand.getText().toString().trim());
                updatedComputer.setModel(model.getText().toString().trim());
                updatedComputer.setScreen(screen.getText().toString().trim());
                sendComputer(updatedComputer);
            }
        });
    }

    private void sendComputer(Computer computer) {

        // RxJava
        apiService.modifyComputer(computer.getId(), computer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Computer>() {

                    CompositeDisposable singleSaveCompositeDisposable = new CompositeDisposable();

                    @Override
                    public void onSubscribe(Disposable d) {
                        singleSaveCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Computer computer) {
                        showResponse("New fields updated: " + computer.toString());
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
