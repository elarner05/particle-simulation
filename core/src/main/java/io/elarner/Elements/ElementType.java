package io.elarner.Elements;

public enum ElementType {
    SAND,
    STONE,
    WATER,
    EMPTY;

    public ElementType next() {
        ElementType[] values = ElementType.values();
        int nextOrdinal = (this.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }
}
