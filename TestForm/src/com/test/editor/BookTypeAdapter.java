package com.test.editor;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.test.comps.TPanel;

import java.io.IOException;

public class BookTypeAdapter extends TypeAdapter<TPanel> {


    @Override
    public TPanel read(final JsonReader in) throws IOException {
        final TPanel comp = new TPanel();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name":
                    comp.setName(in.nextString());
                    break;
                case "bounds": {
                    String[] ret = in.nextString().split(",");
                    if (ret.length == 4) {
                        comp.setBounds(Double.valueOf(ret[0]).intValue(), Double.valueOf(ret[1]).intValue(), Double.valueOf(ret[2]).intValue(), Double.valueOf(ret[3]).intValue());
                    }
                    break;
                }
                case "children":
                    break;
            }
        }
        in.endObject();

        return comp;
    }

    @Override
    public void write(final JsonWriter out, final TPanel comp) throws IOException {
        out.beginObject();
        out.name("name").value(comp.getName());
        out.name("bounds").value(comp.getBounds().getX() + "," + comp.getBounds().getY() + "," + comp.getBounds().getWidth() + "," + comp.getBounds().getHeight());
        out.endObject();
    }
}
