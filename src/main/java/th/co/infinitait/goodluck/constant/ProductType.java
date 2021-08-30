package th.co.infinitait.goodluck.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {
    E("TYPE_EQUIPMENT"),
    I("TYPE_INTERNET_USER"),
    S("TYPE_SERVICE");

    private final String description;

}
