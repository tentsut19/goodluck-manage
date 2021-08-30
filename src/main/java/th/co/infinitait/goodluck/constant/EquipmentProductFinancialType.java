package th.co.infinitait.goodluck.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EquipmentProductFinancialType {
    A("Asset"),
    C("Cost");

    private final String description;

}
