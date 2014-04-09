package net.clonecomputers.lab.sphere.graph;

import java.awt.*;
import java.lang.reflect.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;

public class Grapher extends JPanel {
	JFreeChart chart;
	private FastScatterPlot plot;
	private float[][] data;
	private int size;
	private float minimum = Float.MAX_VALUE;
	
	public Grapher() {
		data = new float[2][4000];
		plot = new FastScatterPlot();
		//plot.setDomainAxis(new LogarithmicAxis("X"));
		plot.setRangeAxis(new LogarithmicAxis("Y"));
		plot.getDomainAxis().setAutoRange(true);
		plot.getRangeAxis().setAutoRange(true);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		plot.getRangeAxis().setTickMarksVisible(true);
		plot.getDomainAxis().setTickMarksVisible(true);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinesVisible(true);
		/*plot.setData(new float[][]{
				{.1f,.2f,.3f,.4f,.5f},
				{.1f,.2f,.3f,.4f,.5f},
		});*/
		chart = new JFreeChart(plot);
		chart.setTitle("Test Plot");
		//data = new ArrayList<float[]>();
	}
	
	public void updateChart() {
		float[][] tmpData = new float[2][size];
		System.arraycopy(data[0],0,tmpData[0],0,size);
		System.arraycopy(data[1],0,tmpData[1],0,size);
		for(int i = 0; i < tmpData[1].length; i++) {
			tmpData[1][i] = tmpData[1][i] - minimum;
		}
		plot.setData(tmpData);
		try {
			forcePlotToRecalculateDataRanges();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		//System.out.println(Arrays.deepToString(plot.getData()));
		//System.out.println(plot.getDataRange(plot.getRangeAxis()));
		plot.getDomainAxis().setRange(plot.getDataRange(plot.getDomainAxis()), false, true);
		plot.getRangeAxis().setRange(plot.getDataRange(plot.getRangeAxis()), false, true);
		this.repaint();
	}
	
	private void forcePlotToRecalculateDataRanges() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		Class<? extends FastScatterPlot> plotClass = plot.getClass();
		Method calculateXDataRange = plotClass.getDeclaredMethod("calculateXDataRange", plot.getData().getClass());
		Method calculateYDataRange = plotClass.getDeclaredMethod("calculateYDataRange", plot.getData().getClass());
		calculateXDataRange.setAccessible(true);
		calculateYDataRange.setAccessible(true);
		Range xRange = (Range) calculateXDataRange.invoke(plot, (Object)plot.getData());
		Range yRange = (Range) calculateYDataRange.invoke(plot, (Object)plot.getData());
		Field xRangeField = plotClass.getDeclaredField("xDataRange");
		Field yRangeField = plotClass.getDeclaredField("yDataRange");
		xRangeField.setAccessible(true);
		yRangeField.setAccessible(true);
		xRangeField.set(plot, xRange);
		yRangeField.set(plot, yRange);
	}
	
	public void addToGraph(float x, float y) {
		if(size >= data[0].length) {
			float[][] tmpData = new float[2][size*2];
			System.arraycopy(data[0], 0, tmpData[0], 0, size);
			System.arraycopy(data[1], 0, tmpData[1], 0, size);
			data = tmpData;
		}
		data[0][size] = x;
		data[1][size] = y;
		size++;
		if(y < minimum) minimum = y;
	}
	
	public void addToGraph(double x, double y) {
		addToGraph((float)x, (float)y);
	}
	
	public void addToGraph(float[] xy) {
		addToGraph(xy[0],xy[1]);
	}
	
	public void addToGraph(double[] xy) {
		addToGraph((float)xy[0],(float)xy[1]);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		plot.draw((Graphics2D)g, this.getVisibleRect(), new Point(0,0), new PlotState(), 
				new PlotRenderingInfo(new ChartRenderingInfo()));
		//chart.draw((Graphics2D)g, this.getVisibleRect());
	}
	
	public static void main(String[] args) {
		Grapher g = new Grapher();
		float[] data = new float[2];
		for(int i = 0; i < 9000; i++) {
			data[0] = i/200f;
			data[1] = (float)Math.sin(i/100f/Math.PI);
			g.addToGraph(data);
		}
		g.updateChart();
		//g.addToGraph(new double[5]);
		/*g.addToGraph(new double[]{
				1,3.3,5,7,2,5,7,9
		});
		g.addToGraph(new double[]{
				100,1000,500,370,740,37,74,10
		});*/
		
		JFrame f = new JFrame("Grapher test");
		f.setContentPane(g);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setSize(600,600);
		f.setVisible(true);
	}
}
