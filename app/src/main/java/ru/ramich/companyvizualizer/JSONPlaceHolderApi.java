package ru.ramich.companyvizualizer;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.ramich.companyvizualizer.models.Company;
import ru.ramich.companyvizualizer.models.Department;
import ru.ramich.companyvizualizer.models.Worker;

public interface JSONPlaceHolderApi {

    //---------------API for Companies-----------------

    @POST("/structure")
    public Call<Company> addCompany(@Body Company company);

    @GET("/structure")
    public Call<List<Company>> getAllCompanies();

    @GET("/structure/{id}")
    public Call<Company> getCompanyById(@Path("id") int id);

    @PUT("/structure/{id}")
    public Call<Company> updateCompany(@Body Company company, @Path("id") int id);

    @DELETE("/structure/{id}")
    public Call<Void> deleteCompany(@Path("id") int id);

    //---------------API for Departments-----------------

    @POST("/structure/{companyId}/department")
    public Call<Department> addDepartment(@Path("companyId") int companyId, @Body Department department);

    @GET("/structure/company/{id}/department")
    public Call<List<Department>> getAllDepartmentsByCompany(@Path("id") int id);

    @GET("/structure/department/{id}")
    public Call<Department> getDepartmentById(@Path("id") int id);

    @PUT("/structure/department/{id}")
    public Call<Department> updateDepartment(@Body Department department, @Path("id") int id);

    @DELETE("/structure/department/{id}")
    public Call<Void> deleteDepartment(@Path("id") int id);

    //---------------API for Workers-----------------

    @POST("/structure/{comId}/department/{depId}/worker")
    public Call<Worker> addWorker(@Path("comId") int comId, @Path("depId") int depId, @Body Worker worker);

    @GET("/structure/department/worker/{id}")
    public Call<List<Worker>> getAllWorkersByDepartment(@Path("id") int id);

    @GET("/structure/worker/{id}")
    public Call<Worker> getWorkerById(@Path("id") int id);

    @PUT("/structure/worker/{id}")
    public Call<Worker> updateWorker(@Body Worker worker, @Path("id") int id);

    @DELETE("/structure/worker/{id}")
    public Call<Void> deleteWorker(@Path("id") int id);
}

