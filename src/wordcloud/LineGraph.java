package wordcloud;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineGraph extends ApplicationFrame
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultCategoryDataset dataset;
	public LineGraph( String applicationTitle )
	{
		super(applicationTitle);

		dataset = new DefaultCategoryDataset( );
	}
	
	public void readyGraph(String chartTitle){
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Hash Algorithm","Time (ms)",
				dataset,
				PlotOrientation.VERTICAL,
				true,true,false);
		
		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		setContentPane( chartPanel );
		this.showGraph();
	}

	public void addData( String name, int time )
	{
		dataset.addValue( time, "hashes" , name );
	}
	
	public void showGraph(){
	      this.pack( );
	      RefineryUtilities.centerFrameOnScreen( this );
	      this.setVisible( true );
	}
}