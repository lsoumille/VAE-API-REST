package controllers;

import services.CryptoService;
import spark.Spark;
import utils.JsonUtil;
import utils.ResponseError;

import static spark.Spark.after;
import static spark.Spark.exception;
import static utils.JsonUtil.toJson;

/**
 * Created by Thales on 13/09/2017.
 */
public class CryptoController {

    public CryptoController(final CryptoService cryptoService) {

        //Encryption URL
        Spark.post("/crypt/:pin/", (request, response) -> cryptoService.encryptMessage(
                request.params(":pin"),
                request.queryParams("keyname"),
                request.queryParams("message")
        ), JsonUtil.json());

        //Decryption URL
        Spark.get("/decrypt/:pin/:keyname/:cryptedData",(request, response) -> cryptoService.decryptMessage(
                request.params(":pin"),
                request.params(":keyname"),
                request.params(":cryptedData")
        ), JsonUtil.json());

        //Digest URL
        Spark.post("digest/:pin", (request, response) -> cryptoService.digestMessage(
                request.params(":pin"),
                request.queryParams("keyname"),
                request.queryParams("message")
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
