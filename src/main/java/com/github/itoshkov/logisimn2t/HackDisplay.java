package com.github.itoshkov.logisimn2t;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.*;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static com.cburch.logisim.data.BitWidth.ONE;

public class HackDisplay extends ManagedComponent {
    static final HackDisplayFactory FACTORY = new HackDisplayFactory();
    private static final BitWidth BIT_WIDTH_13 = BitWidth.create(13);
    private static final BitWidth BIT_WIDTH_16 = BitWidth.create(16);

    private enum Pin {
        CLK(-220, 130, ONE, EndData.INPUT_ONLY),
        WE(-200, 130, ONE, EndData.INPUT_ONLY),
        ADDR(-140, 130, BIT_WIDTH_13, EndData.INPUT_ONLY),
        DATA_IN(-130, 130, BIT_WIDTH_16, EndData.INPUT_ONLY),
        DATA_OUT(-120, 130, BIT_WIDTH_16, EndData.OUTPUT_ONLY),
        RST(-240, 130, ONE, EndData.INPUT_ONLY);

        final int transX;
        final int transY;
        final BitWidth width;
        final int type;

        Pin(int transX, int transY, BitWidth width, int type) {
            this.transX = transX;
            this.transY = transY;
            this.width = width;
            this.type = type;
        }
    }

    private static final Set<Pin> ORDINARY_PINS;

    static {
        final EnumSet<Pin> pins = EnumSet.allOf(Pin.class);
        pins.remove(Pin.CLK);
        ORDINARY_PINS = Collections.unmodifiableSet(pins);
    }

    public HackDisplay(Location loc, AttributeSet attrs) {
        super(loc, attrs, Pin.values().length);
        for (Pin p : Pin.values())
            setEnd(p.ordinal(), getLocation().translate(p.transX, p.transY), p.width, p.type);
    }

    @Override
    public ComponentFactory getFactory() {
        return FACTORY;
    }

    public void draw(ComponentDrawContext context) {
        final Location loc = getLocation();
        final State s = getState(context.getCircuitState());
        draw(context, loc.getX(), loc.getY(), s);
    }

    private State getState(CircuitState circuitState) {
        State state = (State) circuitState.getData(this);
        if (state == null) {
            state = new State(new BufferedImage(512, 256, BufferedImage.TYPE_BYTE_BINARY));
            circuitState.setData(this, state);
        }

        return state;
    }

    private void draw(ComponentDrawContext context, int x, int y, State state) {
        final Graphics g = context.getGraphics();
        x -= 270;
        y -= 140;
        g.drawRoundRect(x, y, 525, 269, 6, 6);

        for (Pin p : ORDINARY_PINS)
            context.drawPin(this, p.ordinal());

        g.setColor(Color.lightGray);
        g.drawRect(x + 6, y + 6, 513, 257);
        context.drawClock(this, 0, Direction.NORTH);
        g.drawImage(state.img, x + 7, y + 7, null);
    }

    @Override
    public void propagate(CircuitState circuitState) {
        final State state = getState(circuitState);

        final int addr = val(circuitState, Pin.ADDR).toIntValue();
        final int data = val(circuitState, Pin.DATA_IN).toIntValue();
        final int x = addr % 32;
        final int y = addr / 32;
        if (state.tick(val(circuitState, Pin.CLK)) && val(circuitState, Pin.WE) == Value.TRUE)
            for (int i = 0; i < 16; i++) {
                final int color = ((data >> i) & 1) - 1;
                state.img.setRGB(16 * x + i, y, color);
            }

        if (val(circuitState, Pin.RST) == Value.TRUE)
            state.reset();

        final Value value = getPixels(state.img, x, y);
        circuitState.setValue(getEndLocation(Pin.DATA_OUT), value, this, 0);
    }

    private Value getPixels(BufferedImage img, int x, int y) {
        if (x < 0 || 16 * x + 15 >= img.getTileWidth() || y < 0 || y >= img.getHeight())
            return Value.createError(BIT_WIDTH_16);

        int out = 0;
        for (int i = 0; i < 16; i++) {
            final int rgb = img.getRGB(16 * x + 15 - i, y);
            final int bit = ~rgb & 1;
            out = (out << 1) | bit;
        }

        return Value.createKnown(BIT_WIDTH_16, out);
    }

    private Value val(CircuitState s, Pin pin) {
        return s.getValue(getEndLocation(pin));
    }

    private Location getEndLocation(Pin pin) {
        return getEndLocation(pin.ordinal());
    }

    private static class State implements ComponentState, Cloneable {
        public Value lastClock = null;
        public BufferedImage img;

        State(BufferedImage img) {
            this.img = img;
            reset();
        }

        public void reset() {
            final Graphics g = img.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 512, 256);
        }

        public State clone() {
            try {
                return (State) super.clone();
            } catch (CloneNotSupportedException var2) {
                return null;
            }
        }

        public boolean tick(Value clk) {
            final boolean rising = lastClock == null || lastClock == Value.FALSE && clk == Value.TRUE;
            lastClock = clk;
            return rising;
        }
    }

    private static class HackDisplayFactory extends AbstractComponentFactory {
        @Override
        public String getName() {
            return "HACK Video Display";
        }

        public String getDisplayName() {
            return "HACK Video Display";
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new HackDisplay(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            return Bounds.create(-270, -140, 526, 270);
        }

        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            final Graphics g = context.getGraphics();
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, y, 15, 15, 3, 3);
            g.setColor(Color.BLUE);
            g.fillRect(x + 3, y + 3, 10, 10);
            g.setColor(Color.BLACK);
        }
    }
}
