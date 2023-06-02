package com.example.CodeGeneratieRestAPI.interfaces;

public interface IRepositoryModel {
    public <T> boolean checkIfObjectExistsByIdentifier(T object);
}
