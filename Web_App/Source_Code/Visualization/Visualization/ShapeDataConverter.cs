using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using ESRI.ArcGIS.Client;
using ESRI.ArcGIS.Client.Geometry;
using ESRI.ArcGIS.Client.Tasks;
using System.Windows.Data;
using ESRI.ArcGIS.Client.Toolkit.DataSources;


namespace Visualization
{
    public class ShapeDataConverter:IValueConverter
    {
        private string datasetId;
        public ShapeDataConverter(string datasetId)
        {
            this.datasetId = datasetId;
            QueryRoutes(datasetId);
        }

        private void QueryRoutes(string datasetId)
        {
            QueryTask routeQueryTask =
                new QueryTask("http://dotsd7gisdev.d7.dot.state.fl.us/ArcGIS/rest/services/Public_View/MapServer/10");
            routeQueryTask.ExecuteCompleted += RouteQueryTask_ExecuteCompleted;
            routeQueryTask.Failed += QueryTask_Failed;

            ESRI.ArcGIS.Client.Tasks.Query routeQuery = new ESRI.ArcGIS.Client.Tasks.Query();
            routeQuery.Where = "DATASET_ID = " + datasetId;
            routeQuery.OutFields.Add("*");
            routeQuery.ReturnGeometry = false;
            routeQueryTask.ExecuteAsync(routeQuery);
        }

        void RouteQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;

            for (int i = 1; i < featureSet.Features.Count; i++)
            {
                DateTime dt = System.Convert.ToDateTime(featureSet.ElementAt(i).Attributes["COLLECTED_DATE"].ToString());
            }
        }

        // This is the function that does the work of the conversion.
        public object Convert(object value, System.Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            // Create a variable that will be used to return something.
            string theReturnValue = null;

            // Ensure we have the KmlExtendedData in an IList.
            if (value is IList<ESRI.ArcGIS.Client.Toolkit.DataSources.Kml.KmlExtendedData>)
            {
                // Cast the input 'value' object of the converter to the correct Type.
                IList<ESRI.ArcGIS.Client.Toolkit.DataSources.Kml.KmlExtendedData> theIList = (IList<ESRI.ArcGIS.Client.Toolkit.DataSources.Kml.KmlExtendedData>)value;

                // Obtain the first KmlExtendedData object from the IList.
                ESRI.ArcGIS.Client.Toolkit.DataSources.Kml.KmlExtendedData theKmlExtendedData = theIList.FirstOrDefault();

                // Depending on what passed as the ConverterParameter (which is the input argument 'parameter') in XAML will 
                // determine what we Return back. The options are: 'Value', 'DisplayName', and 'Name'.
                if (parameter.ToString() == "Value")
                {
                    theReturnValue = theKmlExtendedData.Value;
                }
                else if (parameter.ToString() == "DisplayName")
                {
                    theReturnValue = theKmlExtendedData.DisplayName;
                }
                else if (parameter.ToString() == "Name")
                {
                    theReturnValue = theKmlExtendedData.Name;
                }
            }
            // Return something back.
            return theReturnValue;
        }

        // This function is necessary because we implement the Data.IValueConverter Interface. Hence we must have the signature
        // defined even though we will not really be doing any ConvertBack operations in this example.
        public object ConvertBack(object value, System.Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new System.NotImplementedException();
        }

        private void QueryTask_Failed(object sender, TaskFailedEventArgs args)
        {
            MessageBox.Show("Query execute error: " + args.Error);
        }
    }
}
