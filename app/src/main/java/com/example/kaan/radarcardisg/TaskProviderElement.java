package com.example.kaan.radarcardisg;


public class TaskProviderElement {
    public String departmentName;
    public String companyName;
    public String startDate;
    public String finishDate;
    public String listId;
    public String tkId;

    public TaskProviderElement(String departmentName,String companyName, String startDate, String finishDate,String listId,String tkId){
        this.departmentName = departmentName;
        this.companyName = companyName;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.listId = listId;
        this.tkId = tkId;
    }

    public String getDepartmentName(){
        return departmentName;
    }
    public String getCompanyName(){
        return companyName;
    }
    public String getStartDate(){
        return startDate;
    }

    public String getFinishDate(){ return finishDate;}
    public String getListId(){ return listId;}
    public String getTkId(){ return tkId; }
}
