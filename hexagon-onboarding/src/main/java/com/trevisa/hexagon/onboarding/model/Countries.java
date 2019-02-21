package com.trevisa.hexagon.onboarding.model;

public abstract class Countries {
    static final Country BR = new Country("BR");

    private Countries() {
    }

    public static Country fromAlpha2(String alpha2) {
        if (BR.getAlpha2().equals(alpha2)) {
            return BR;
        }

        return null;
    }
}
