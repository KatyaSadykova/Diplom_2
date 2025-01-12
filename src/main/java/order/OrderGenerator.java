package order;

import data.Order;

public class OrderGenerator {
    public static Order getListOrder() {

        return new Order()
                .setIngredients(OrderClient.getAllIngredients().extract().path("data._id"));
    }
}
