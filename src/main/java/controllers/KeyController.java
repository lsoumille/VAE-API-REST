package controllers;

import services.KeyService;
import spark.Spark;
import utils.JsonUtil;
import utils.ResponseError;

import static spark.Spark.after;
import static spark.Spark.exception;
import static utils.JsonUtil.toJson;

/**
 * Created by Thales on 13/09/2017.
 */
public class KeyController {

    public KeyController(final KeyService keyService) {

        //Create Key URL
        Spark.post("/keys/:pin", (request, response) -> keyService.createKey(
                request.params(":pin"),
                request.queryParams("keyname")
        ), JsonUtil.json());

        //Get Key URL
        Spark.get("/keys/:pin/:keyname",(request, response) -> keyService.getKey(
                request.params(":pin"),
                request.params("keyname")
        ), JsonUtil.json());

        //Delete Key URL
        Spark.delete("/keys/:pin", (request, response) -> keyService.getKey(
                request.params(":pin"),
                request.queryParams("keyname")
        ));

        after((req, res) -> {
            res.type("application/json");
        });

        exception(RuntimeException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });
    }
}
