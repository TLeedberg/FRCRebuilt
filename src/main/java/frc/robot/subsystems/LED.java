package frc.robot.subsystems;

import frc.robot.testingdashboard.SubsystemBase;
import frc.robot.utils.FieldUtils;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

public class LED extends SubsystemBase{
    public static class LEDPattern {
        public enum PatternType {
            SOLID,
            GRADIENT,
            BLINK,
            CHECKERED,
            CHECKERED_BLINK,
            SLIDE,
            RAINBOW,
            RUNNABLE
        }
        private PatternType m_patternType;
        private Color m_foreground;
        private Color m_background;
        private int m_speed;

        public LEDPattern(PatternType patternType, Color foreground, Color background, int speed) {
            m_patternType = patternType;
            m_foreground = foreground;
            m_background = background;
            m_speed = speed;
        }

        /**
         * Calculates a color from the LEDPattern's pattern type, stored colors, and speed.
         * @param lightIndex The index of the LED whose color is being calculated.
         * @param tick The time in MS. Should be the same between every call at one time.
		 * @param numLights The number of lights in the string of LEDs.
         * @return A color to assign to the LED.
         */
        public Color getColor(int lightIndex, int tick, int numLights) {
            switch (m_patternType) {
                case SOLID:
                    return m_foreground;
                case GRADIENT:
                    double dt = (tick*m_speed)/1000.0+(double)lightIndex/numLights;
                    return Color.lerpRGB(m_foreground, m_background, (-Math.cos(Math.PI*2*dt)+1)/2);
                case BLINK: 
                    return (((tick*m_speed)/1000)%2 == 0) ? m_foreground : m_background;
                case CHECKERED:
                    return (lightIndex%2 == 0) ? m_foreground : m_background;
                case CHECKERED_BLINK:
                    return (((tick*m_speed)/1000+lightIndex)%2 == 0) ? m_foreground : m_background;
                case SLIDE:
                    return (((tick*m_speed)/1000+lightIndex)%4 == 0) ? m_foreground : m_background;
                case RAINBOW:
                    return Color.fromHSV(((tick*m_speed)/1000)%180, 255, 255);
                case RUNNABLE:
                    return runnable(lightIndex, tick, numLights);
            }
            return m_foreground;
        }

        /**
		 * Overridable method; extend LEDPattern and override runnable() to program
		 * the pattern. Made as a separate method so getColor doesn't have to be
		 * overriden, so the built-in patterns can be kept.
         * @param lightIndex The index of the LED whose color is being calculated.
         * @param tick The time in MS. Should be the same between every call at one time.
		 * @param numLights The number of lights in the string of LEDs.
         * @return A color to assign to the LED.
		 */
        public Color runnable(int lightIndex, int tick, int numLights) {return Color.kWhite;}
    }

    private static LED m_LEDLights = null;

    private AddressableLED m_LED;
    private AddressableLEDBuffer m_LEDBuffer;

    private LEDPattern[] m_sections;

    private LED() {
        super("LED");

        m_LED = new AddressableLED(cfgInt("LEDPort"));

        m_LEDBuffer = new AddressableLEDBuffer(cfgInt("LEDCount"));
        m_LED.setLength(cfgInt("LEDCount"));

        m_sections = new LEDPattern[3];

        m_LED.start();
    }

    public static LED getInstance() {
        if (m_LEDLights == null) {
            m_LEDLights = new LED();
        }
        return m_LEDLights;
    }

    public void setPattern(int section, LEDPattern pattern) {
        if (section < 0 || section >= m_sections.length) return;
        m_sections[section] = pattern;
    }

    public void gameStateLights() {
        FieldUtils.GameState gameState = FieldUtils.getInstance().getGameState();
        double currentMatchTime = FieldUtils.getInstance().stateTimeLeft();
        if (currentMatchTime < cfgDbl("stateChangeWarningTime")){
            //something to alter the existing colors
        }

        switch (gameState){
            case AUTO:
                break;
            case TRANSITION:
                break;
            case RED_START:
                break;
            case BLUE_START:
                break;
            case ENDGAME:
                break;
            default: 
                break;
        }
    }

    @Override
    public void periodic() {
        int tick = (int)(Timer.getFPGATimestamp()*1000);
        for (int i = 0; i < m_sections.length; i++) {
            int startIndex = (m_LEDBuffer.getLength()*i/m_sections.length);
            int endIndex = (m_LEDBuffer.getLength()*(i+1)/m_sections.length);
            for (int j = startIndex; j < endIndex; j++) {
                Color color = m_sections[i].getColor(j, tick, m_LEDBuffer.getLength());
                m_LEDBuffer.setLED(j, color);
            }
        }
        m_LED.setData(m_LEDBuffer);
        super.periodic();
    }
}
