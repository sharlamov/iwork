package md.sh.enums;

public enum Band {

    TITLE("TITLE"), DETAIL1("DETAIL1"), SUMMARY("SUMMARY"), GROUPH("GROUPH"), GROUPF("GROUPF"), GROUP("GROUP");

    private String name;

    Band(String name) {
        this.name = name.toUpperCase();
    }

    @Override
    public String toString() {
        return name;
    }
}
