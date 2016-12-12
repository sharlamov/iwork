package com.soapservice.utils;

import com.soapservice.model.*;

import java.math.BigDecimal;
import java.math.BigInteger;


public class TestUtility {

    public Menu GET_MENU() {
        Menu response = new Menu();
        for (int i = 0; i < 3; i++) {
            Item item = new Item();
            item.setId(i);
            item.setCategoryId(i + 100);
            response.getItem().add(item);
        }
        return response;
    }

    public Categories GET_CATEGORIES() {
        Categories response = new Categories();
        Category category1 = new Category();
        category1.setId(1);
        category1.setName(getLabel("Бургер", "Burgers", "Burgers"));
        response.getCategory().add(category1);

        Category category2 = new Category();
        category2.setId(2);
        category2.setName(getLabel("Закуски", "Gustari", "Snacks"));
        response.getCategory().add(category2);
        return response;
    }

    public Components GET_COMPONENTS() {
        Components response = new Components();
        com.soapservice.model.Component component = new com.soapservice.model.Component();
        component.setId(1);
        component.setName(getLabel("Двойной гамбургер c говядиной", "Carne de vita dubla burger", "Double beef burger"));
        response.getComponent().add(component);
        return response;
    }

    public Discounts GET_DISCOUNTS() {
        Discounts response = new Discounts();
        Discount discount1 = new Discount();
        discount1.setId(1);
        discount1.setNumber(123456678945879L);
        discount1.setPercent(25);
        response.getDiscount().add(discount1);

        Discount discount2 = new Discount();
        discount2.setId(2);
        discount2.setNumber(987654321);
        discount2.setPercent(17);
        response.getDiscount().add(discount2);
        return response;
    }

    public Elements GET_ELEMENTS() {
        Elements response = new Elements();
        Element element = new Element();
        element.setId(1);
        element.setCategory(12);
        element.setName(getLabel("БУРГЕР ИЗ ГОВЯДИНЫ", "BUGER CU CARNE DE VITA", "BEEF BURGER"));
        element.setPrice(new BigDecimal(45.00));
        element.setSpecialPrice(new BigDecimal(40.00));
        element.setAlgorithm(Algorithm.STANDART);
        element.setWeight(500);
        element.setDescription(getLabel("Это описание товара. Это описание товара. Это описание товара. Это описание товара. Это описание товара. Это описание товара.", "Aceasta descriere a marfurilor. Aceasta descriere a marfurilor. Aceasta descriere a marfurilor. Aceasta descriere a marfurilor. Aceasta descriere a marfurilor. Aceasta descriere a marfurilor.", "This description of the goods. This description of the goods. This description of the goods. This description of the goods. This description of the goods. This description of the goods."));

        com.soapservice.model.Url url = new com.soapservice.model.Url();
        url.setPath("my_big_photo1.png");
        Photo photo = new Photo();
        photo.setUrl(url);
        element.getPhotos().getPhoto().add(photo);

        for (int i = 0, price = 50; i < 4; i++) {
            com.soapservice.model.Component component = new com.soapservice.model.Component();
            component.setId(i + 1);
            component.setPrice(new BigDecimal(price));
            component.setSpecialPrice(new BigDecimal(price - 5));
            element.getComponents().getComponent().add(component);
        }

        response.getElement().add(element);
        return response;
    }

    public Orders GET_ORDER_STATUS() {
        Orders response = new Orders();
        Order order = new Order();
        order.setId(1);
        order.setStatusId(new BigInteger("12"));
        response.getOrder().add(order);
        return response;
    }

    public Statuses GET_STATUSES() {
        Statuses response = new Statuses();
        Status status1 = new Status();
        status1.setId(1);
        status1.setName(getLabel("Подтвержден", "Adoptat", "Accepted"));
        response.getStatus().add(status1);

        Status status2 = new Status();
        status2.setId(2);
        status2.setName(getLabel("Завершен", "Terminat", "Completed"));
        response.getStatus().add(status2);

        return response;
    }

    private Label getLabel(String ru, String ro, String en) {
        Label lb = new Label();
        lb.setRu(ru);
        lb.setRo(ro);
        lb.setEn(en);
        return lb;
    }
}