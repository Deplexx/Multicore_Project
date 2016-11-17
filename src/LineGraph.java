package wordcloud;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Utilizing JFreeChart dependency.
 * 
 * This class serves as a simple access to the JFreeChart for the purpose of the Word Cloud program.
 */
public class LineGraph extends ApplicationFrame
{
	private static final long serialVersionUID = 1L;
	private DefaultCategoryDataset dataset;
	/**
	 * Initialize the class with a string that will show as application title and initialize the
	 * dataset stored within the object.
	 * 
	 * @param applicationTitle : Application Title to display on the Chart Window.
	 */
	public LineGraph( String applicationTitle )
	{
		super(applicationTitle);

		dataset = new DefaultCategoryDataset( );
	}
	
	/**
	 * Once the user has put new data (using {@link #addData(String, int)}), user can call readyGraph
	 * with the chart title. This will get ready all of the required settings for the JFreeChart and
	 * display the chart.
	 * 
	 * @param chartTitle - Name of the chart.
	 */
	public void readyGraph(String chartTitle){
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Hash Algorithm","Time (ms)",
				dataset,
				PlotOrientation.VERTICAL,
				true,true,false);
		
		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 800 , 500 ) );
		setContentPane( chartPanel );
		this.showGraph();
	}

	/**
	 * Adds a name (x-axis) and time (y-axis) for the chart. Used by Word Cloud to distinguish different
	 * hash algorithms vs time it took to run.
	 * 
	 * @param name - Name of the hash algorithm.
	 * @param time - Integer time of how long it took in milliseconds.
	 */
	public void addData( String name, int time )
	{
		dataset.addValue( time, "hashes" , name );
	}
	
	/**
	 * Sets the graph to visible. Used by {@link #readyGraph(String)}.
	 */
	public void showGraph(){
	      this.pack( );
	      RefineryUtilities.centerFrameOnScreen( this );
	      this.setVisible( true );
	}
}