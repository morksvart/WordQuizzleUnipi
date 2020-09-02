package org.baratta;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class Router {
    private final Scene scene;
    private String username;

    public Router(Scene scene) {
        this.scene = scene;
    }

    public void login(String username) {
        this.username=username;
        showDashboard(username);
    }

    private void showDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("Dashboard.fxml")
            );
            scene.setRoot((Parent) loader.load());
            DashboardController ctrl =
                    loader.<DashboardController>getController();
            ctrl.initDashboard(this, username);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void logout() {
        showLogin();
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("Login.fxml")
            );
            scene.setRoot((Parent) loader.load());
            LoginController ctrl =
                    loader.<LoginController>getController();
            ctrl.initLogin(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("Registration.fxml")
            );
            scene.setRoot((Parent) loader.load());
            RegistrationController ctrl =
                    loader.<RegistrationController>getController();
            ctrl.initRegistration(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}
