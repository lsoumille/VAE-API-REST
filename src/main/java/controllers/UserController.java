package controllers;

import business.User;
import services.UserService;import spark.Spark;
import utils.JsonUtil;

import static spark.Spark.*;
import static utils.JsonUtil.json;
import static utils.JsonUtil.toJson;

/**
 * Created by Thales on 12/09/2017.
 */
public class UserController {

    public UserController(final UserService userService) {

        Spark.get("/users", (req, res) -> userService.getAllUsers(), JsonUtil.json());

        Spark.get("/users/:id", (req, res) -> {
            String id = req.params(":id");
            User user = userService.getUser(id);
            if (user != null) {
                return user;
            }
            res.status(400);
            return new String("No user with id '%s' found");
        }, JsonUtil.json());

        Spark.post("/users", (req, res) -> userService.createUser(
                req.queryParams("name"),
                req.queryParams("email")
        ), JsonUtil.json());

        Spark.put("/users/:id", (req, res) -> userService.updateUser(
                req.params(":id"),
                req.queryParams("name"),
                req.queryParams("email")
        ), JsonUtil.json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body();
        });
    }
}
