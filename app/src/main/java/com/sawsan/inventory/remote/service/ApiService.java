package com.sawsan.inventory.remote.service;

import com.sawsan.inventory.data.model.Computer;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("computers/")
    Observable<Computer[]> getComputersData();

    @GET("computers/{computer_id}")
    Single<Computer> getComputerData(@Path("computer_id") String computer_id);

    @POST("/computers")
    Single<Computer> insertComputer(@Body Computer computer);

    @PUT("/computers/{computer_id}")
    Single<Computer> modifyComputer(@Path("computer_id") String computer_id, @Body Computer computer);

    @DELETE("/computers/{computer_id}")
    Single<String> deleteComputer(@Path("computer_id") String computer_id);
}
