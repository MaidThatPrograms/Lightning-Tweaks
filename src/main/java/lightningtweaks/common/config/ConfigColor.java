package lightningtweaks.common.config;

import java.awt.Color;

public class ConfigColor {
    private float[] color = new float[] { .45F, .45F, .5F, .3F };

    public float alpha() {
	return color[3];
    }

    public float blue() {
	return color[2];
    }

    public float green() {
	return color[1];
    }

    public float red() {
	return color[0];
    }

    public void set(Color color) {
	this.color = color.getComponents(this.color);
    }
}
