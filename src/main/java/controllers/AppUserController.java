package controllers;

import services.AppUserService;
import spark.Spark;
import utils.JsonUtil;

public class AppUserController {

    public AppUserController(final AppUserService appUserService) {

        //Encrypt User infos
        Spark.post("/appuser/crypt/:pin", (request, response) -> appUserService.encryptUser(
                request.params(":pin"),
                request.queryParams("keyname"),
                request.queryParams("firstname"),
                request.queryParams("lastname"),
                request.queryParams("address"),
                request.queryParams("city")
        ), JsonUtil.json());

        //Decrypt User infos
        Spark.post("/appuser/decrypt/:pin", (request, response) -> appUserService.decryptUser(
                request.params(":pin"),
                request.queryParams("keyname"),
                request.queryParams("firstname"),
                request.queryParams("lastname"),
                request.queryParams("address"),
                request.queryParams("city")
        ), JsonUtil.json());
    }
}
