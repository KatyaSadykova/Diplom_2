package data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Order {
    private List<String> ingredients;

    public Order setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}
