package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests invocation of a method with the maximum number of parameters a method can have. A method descriptor may
 * declare at most 255 argument slots (JVMS 4.3.3), and one of those slots is taken by {@code this} for an instance
 * method, so a static method taking 255 int parameters is the widest call that can be made.
 *
 * <p>
 * The arguments are unboxed into a fixed-size buffer of 255 jvalues, so this exercises the last entry of that
 * buffer.
 */
@ExtendWith(TestMethodNameLogger.class)
public class NarcissusManyParametersTest {

    /** The maximum number of argument slots in a method descriptor. */
    private static final int MAX_PARAMS = 255;

    /** A class with a static method taking the maximum possible number of parameters. */
    public static class ManyParams {
    static int sum255(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5,
            final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final int p12,
            final int p13, final int p14, final int p15, final int p16, final int p17, final int p18,
            final int p19, final int p20, final int p21, final int p22, final int p23, final int p24,
            final int p25, final int p26, final int p27, final int p28, final int p29, final int p30,
            final int p31, final int p32, final int p33, final int p34, final int p35, final int p36,
            final int p37, final int p38, final int p39, final int p40, final int p41, final int p42,
            final int p43, final int p44, final int p45, final int p46, final int p47, final int p48,
            final int p49, final int p50, final int p51, final int p52, final int p53, final int p54,
            final int p55, final int p56, final int p57, final int p58, final int p59, final int p60,
            final int p61, final int p62, final int p63, final int p64, final int p65, final int p66,
            final int p67, final int p68, final int p69, final int p70, final int p71, final int p72,
            final int p73, final int p74, final int p75, final int p76, final int p77, final int p78,
            final int p79, final int p80, final int p81, final int p82, final int p83, final int p84,
            final int p85, final int p86, final int p87, final int p88, final int p89, final int p90,
            final int p91, final int p92, final int p93, final int p94, final int p95, final int p96,
            final int p97, final int p98, final int p99, final int p100, final int p101, final int p102,
            final int p103, final int p104, final int p105, final int p106, final int p107, final int p108,
            final int p109, final int p110, final int p111, final int p112, final int p113, final int p114,
            final int p115, final int p116, final int p117, final int p118, final int p119, final int p120,
            final int p121, final int p122, final int p123, final int p124, final int p125, final int p126,
            final int p127, final int p128, final int p129, final int p130, final int p131, final int p132,
            final int p133, final int p134, final int p135, final int p136, final int p137, final int p138,
            final int p139, final int p140, final int p141, final int p142, final int p143, final int p144,
            final int p145, final int p146, final int p147, final int p148, final int p149, final int p150,
            final int p151, final int p152, final int p153, final int p154, final int p155, final int p156,
            final int p157, final int p158, final int p159, final int p160, final int p161, final int p162,
            final int p163, final int p164, final int p165, final int p166, final int p167, final int p168,
            final int p169, final int p170, final int p171, final int p172, final int p173, final int p174,
            final int p175, final int p176, final int p177, final int p178, final int p179, final int p180,
            final int p181, final int p182, final int p183, final int p184, final int p185, final int p186,
            final int p187, final int p188, final int p189, final int p190, final int p191, final int p192,
            final int p193, final int p194, final int p195, final int p196, final int p197, final int p198,
            final int p199, final int p200, final int p201, final int p202, final int p203, final int p204,
            final int p205, final int p206, final int p207, final int p208, final int p209, final int p210,
            final int p211, final int p212, final int p213, final int p214, final int p215, final int p216,
            final int p217, final int p218, final int p219, final int p220, final int p221, final int p222,
            final int p223, final int p224, final int p225, final int p226, final int p227, final int p228,
            final int p229, final int p230, final int p231, final int p232, final int p233, final int p234,
            final int p235, final int p236, final int p237, final int p238, final int p239, final int p240,
            final int p241, final int p242, final int p243, final int p244, final int p245, final int p246,
            final int p247, final int p248, final int p249, final int p250, final int p251, final int p252,
            final int p253, final int p254) {
        return p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15 + p16 + p17 +
                p18 + p19 + p20 + p21 + p22 + p23 + p24 + p25 + p26 + p27 + p28 + p29 + p30 + p31 + p32 + p33 +
                p34 + p35 + p36 + p37 + p38 + p39 + p40 + p41 + p42 + p43 + p44 + p45 + p46 + p47 + p48 + p49 +
                p50 + p51 + p52 + p53 + p54 + p55 + p56 + p57 + p58 + p59 + p60 + p61 + p62 + p63 + p64 + p65 +
                p66 + p67 + p68 + p69 + p70 + p71 + p72 + p73 + p74 + p75 + p76 + p77 + p78 + p79 + p80 + p81 +
                p82 + p83 + p84 + p85 + p86 + p87 + p88 + p89 + p90 + p91 + p92 + p93 + p94 + p95 + p96 + p97 +
                p98 + p99 + p100 + p101 + p102 + p103 + p104 + p105 + p106 + p107 + p108 + p109 + p110 + p111 +
                p112 + p113 + p114 + p115 + p116 + p117 + p118 + p119 + p120 + p121 + p122 + p123 + p124 + p125 +
                p126 + p127 + p128 + p129 + p130 + p131 + p132 + p133 + p134 + p135 + p136 + p137 + p138 + p139 +
                p140 + p141 + p142 + p143 + p144 + p145 + p146 + p147 + p148 + p149 + p150 + p151 + p152 + p153 +
                p154 + p155 + p156 + p157 + p158 + p159 + p160 + p161 + p162 + p163 + p164 + p165 + p166 + p167 +
                p168 + p169 + p170 + p171 + p172 + p173 + p174 + p175 + p176 + p177 + p178 + p179 + p180 + p181 +
                p182 + p183 + p184 + p185 + p186 + p187 + p188 + p189 + p190 + p191 + p192 + p193 + p194 + p195 +
                p196 + p197 + p198 + p199 + p200 + p201 + p202 + p203 + p204 + p205 + p206 + p207 + p208 + p209 +
                p210 + p211 + p212 + p213 + p214 + p215 + p216 + p217 + p218 + p219 + p220 + p221 + p222 + p223 +
                p224 + p225 + p226 + p227 + p228 + p229 + p230 + p231 + p232 + p233 + p234 + p235 + p236 + p237 +
                p238 + p239 + p240 + p241 + p242 + p243 + p244 + p245 + p246 + p247 + p248 + p249 + p250 + p251 +
                p252 + p253 + p254;
        }
    }

    @BeforeEach
    public void setUp() {
        if (!Narcissus.libraryLoaded) {
            throw new RuntimeException("Narcissus library not loaded");
        }
    }

    /**
     * Find the widest method.
     *
     * @return the method taking {@link #MAX_PARAMS} int parameters
     * @throws NoSuchMethodException
     *             if the method is not found
     */
    private static Method findSum255() throws NoSuchMethodException {
        final Class<?>[] paramTypes = new Class<?>[MAX_PARAMS];
        Arrays.fill(paramTypes, int.class);
        return Narcissus.findMethod(ManyParams.class, "sum255", paramTypes);
    }

    @Test
    public void testInvokeMethodWithMaximumNumberOfParameters() throws Exception {
        final Method method = findSum255();
        final Object[] args = new Object[MAX_PARAMS];
        for (int i = 0; i < args.length; i++) {
            args[i] = Integer.valueOf(i);
        }
        assertThat(Narcissus.invokeStaticIntMethod(method, args))
                .isEqualTo((MAX_PARAMS - 1) * MAX_PARAMS / 2);
        assertThat(Narcissus.invokeStaticMethod(method, args))
                .isEqualTo(Integer.valueOf((MAX_PARAMS - 1) * MAX_PARAMS / 2));
    }

    @Test
    public void testLastParameterIsCheckedToo() throws Exception {
        final Method method = findSum255();
        final Object[] args = new Object[MAX_PARAMS];
        for (int i = 0; i < args.length; i++) {
            args[i] = Integer.valueOf(i);
        }
        args[MAX_PARAMS - 1] = "not an int";
        try {
            Narcissus.invokeStaticIntMethod(method, args);
            fail("sum255() should have rejected a String as its last argument");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong type");
        }
    }

    @Test
    public void testTooManyArgumentsForTheWidestMethod() throws Exception {
        final Method method = findSum255();
        final Object[] args = new Object[MAX_PARAMS + 1];
        for (int i = 0; i < args.length; i++) {
            args[i] = Integer.valueOf(i);
        }
        try {
            Narcissus.invokeStaticIntMethod(method, args);
            fail("sum255() should have rejected a call with too many arguments");
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("wrong number of arguments");
        }
    }
}
