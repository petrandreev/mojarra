package com.sun.faces.test.servlet30.dynamic;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "issue1414Bean")
@SessionScoped
public class Issue1414Bean implements Serializable {

    public void temporaryMoveComponent(ActionEvent ae) {

        System.out.println("Temporarily moving a component");
        UIComponent button = ae.getComponent();
        UIComponent movefrom = button.findComponent("movefrom");
        UIComponent moveto = button.findComponent("moveto");

        if (movefrom.getChildren().isEmpty()) {
            UIComponent moveme = moveto.getChildren().get(0);
            moveto.getChildren().remove(moveme);
            movefrom.getChildren().add(moveme);
        } else {
            UIComponent moveme = movefrom.getChildren().get(0);
            movefrom.getChildren().remove(moveme);
            moveto.getChildren().add(moveme);
        }
    }
}
