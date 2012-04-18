//Copyright 2012 University of South Florida

//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at

//       http://www.apache.org/licenses/LICENSE-2.0

//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

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

namespace Visualization
{
    public partial class MainPage : UserControl
    {
        // Navigation toolbar
        string _toolMode = "";
        List<Envelope> _extentHistory = new List<Envelope>();
        int _currentExtentIndex = 0;
        bool _newExtent = true;
        Image _previousExtentImage;
        Image _nextExtentImage;
        private Draw MyDrawObject;

        private bool isShapeModifiedQuery = false; // When clicked Find, store the original shapes to prevent future queries
        private GraphicCollection hartOriginalShapes, pstaOriginalShapes;

        // Query
        // [0] = Hart           [1] = Psta
        long[] _datasetId = new long[2]{-1, -1};

        Dictionary<string, Route> hartShapeIdToRoute, pstaShapeIdToRoute;
        Dictionary<string, Route> hartRouteIdToRoute, pstaRouteIdToRoute;

        public MainPage()
        {
            InitializeComponent();

            hartShapeIdToRoute = new Dictionary<string, Route>();
            pstaShapeIdToRoute = new Dictionary<string, Route>();
            hartRouteIdToRoute = new Dictionary<string, Route>();
            pstaRouteIdToRoute = new Dictionary<string, Route>();

            // Navigation toolbar - Zoom in/out
            MyDrawObject = new Draw(MyMap)
            {
                FillSymbol = LayoutRoot.Resources["SelectedRectangle"] as ESRI.ArcGIS.Client.Symbols.FillSymbol,
                DrawMode = DrawMode.Rectangle
            };
            MyDrawObject.DrawComplete += myDrawObject_DrawComplete;

            // Query Agency table - D7TRADM.CUTR4_AGENCY_SERVICE
            QueryTask hartAgencyQueryTask =
                new QueryTask("http://dotsd7gispro.d7.dot.state.fl.us/arcgis/rest/services/Public_View/MapServer/0");
            hartAgencyQueryTask.ExecuteCompleted += HartAgencyQueryTask_ExecuteCompleted;
            hartAgencyQueryTask.Failed += QueryTask_Failed;

            ESRI.ArcGIS.Client.Tasks.Query hartQuery = new ESRI.ArcGIS.Client.Tasks.Query();
            hartQuery.Where = "AGENCY_NAME LIKE '%Hillsborough%'";
            hartQuery.OutFields.Add("*");
            hartQuery.ReturnGeometry = false;
            hartAgencyQueryTask.ExecuteAsync(hartQuery);

            // Query Agency table - D7TRADM.CUTR4_AGENCY_SERVICE
            QueryTask pstaAgencyQueryTask =
                new QueryTask("http://dotsd7gispro.d7.dot.state.fl.us/arcgis/rest/services/Public_View/MapServer/0");
            pstaAgencyQueryTask.ExecuteCompleted += PstaAgencyQueryTask_ExecuteCompleted;
            pstaAgencyQueryTask.Failed += QueryTask_Failed;

            ESRI.ArcGIS.Client.Tasks.Query pstaQuery = new ESRI.ArcGIS.Client.Tasks.Query();
            pstaQuery.Where = "AGENCY_NAME LIKE '%PSTA%'";
            pstaQuery.OutFields.Add("*");
            pstaQuery.ReturnGeometry = false;
            pstaAgencyQueryTask.ExecuteAsync(pstaQuery);
        }

        // Navigation toolbar
        private void MyToolbar_Loaded(object sender, RoutedEventArgs e)
        {
            _previousExtentImage = MyToolbar.Items[3].Content as Image;
            _nextExtentImage = MyToolbar.Items[4].Content as Image;
        }

        // Navigation toolbar
        private void myDrawObject_DrawComplete(object sender, DrawEventArgs args)
        {
            if (_toolMode == "zoomin")
            {
                MyMap.ZoomTo(args.Geometry as ESRI.ArcGIS.Client.Geometry.Envelope);
            }
            else if (_toolMode == "zoomout")
            {
                Envelope currentExtent = MyMap.Extent;

                Envelope zoomBoxExtent = args.Geometry as Envelope;
                MapPoint zoomBoxCenter = zoomBoxExtent.GetCenter();

                double whRatioCurrent = currentExtent.Width / currentExtent.Height;
                double whRatioZoomBox = zoomBoxExtent.Width / zoomBoxExtent.Height;

                Envelope newEnv = null;

                if (whRatioZoomBox > whRatioCurrent)
                // use width
                {
                    double mapWidthPixels = MyMap.Width;
                    double multiplier = currentExtent.Width / zoomBoxExtent.Width;
                    double newWidthMapUnits = currentExtent.Width * multiplier;
                    newEnv = new Envelope(new MapPoint(zoomBoxCenter.X - (newWidthMapUnits / 2), zoomBoxCenter.Y),
                                                   new MapPoint(zoomBoxCenter.X + (newWidthMapUnits / 2), zoomBoxCenter.Y));
                }
                else
                // use height
                {
                    double mapHeightPixels = MyMap.Height;
                    double multiplier = currentExtent.Height / zoomBoxExtent.Height;
                    double newHeightMapUnits = currentExtent.Height * multiplier;
                    newEnv = new Envelope(new MapPoint(zoomBoxCenter.X, zoomBoxCenter.Y - (newHeightMapUnits / 2)),
                                                   new MapPoint(zoomBoxCenter.X, zoomBoxCenter.Y + (newHeightMapUnits / 2)));
                }

                if (newEnv != null)
                    MyMap.ZoomTo(newEnv);
            }
        }

        // Navigation toolbar
        private void MyMap_ExtentChanged(object sender, ExtentEventArgs e)
        {
            if (e.OldExtent == null)
            {
                _extentHistory.Add(e.NewExtent.Clone());
                return;
            }

            if (_newExtent)
            {
                _currentExtentIndex++;

                if (_extentHistory.Count - _currentExtentIndex > 0)
                    _extentHistory.RemoveRange(_currentExtentIndex, (_extentHistory.Count - _currentExtentIndex));

                if (_nextExtentImage.IsHitTestVisible == true)
                {
                    _nextExtentImage.Opacity = 0.3;
                    _nextExtentImage.IsHitTestVisible = false;
                }

                _extentHistory.Add(e.NewExtent.Clone());

                if (_previousExtentImage.IsHitTestVisible == false)
                {
                    _previousExtentImage.Opacity = 1;
                    _previousExtentImage.IsHitTestVisible = true;
                }
            }
            else
            {
                MyMap.IsHitTestVisible = true;
                _newExtent = true;
            }
        }

        // Navigation toolbar
        private void MyToolbar_ToolbarIndexChanged(object sender, ESRI.ArcGIS.Client.Toolkit.SelectedToolbarItemArgs e)
        {
            StatusTextBlock.Text = e.Item.Text;
        }

        // Navigation toolbar
        private void MyToolbar_ToolbarItemClicked(object sender, ESRI.ArcGIS.Client.Toolkit.SelectedToolbarItemArgs e)
        {
            MyDrawObject.IsEnabled = false;
            _toolMode = "";
            switch (e.Index)
            {
                case 0: // ZoomIn Layers
                    MyDrawObject.IsEnabled = true;
                    _toolMode = "zoomin";
                    break;
                case 1: // Zoom Out
                    MyDrawObject.IsEnabled = true;
                    _toolMode = "zoomout";
                    break;
                case 2: // Pan
                    break;
                case 3: // Previous Extent
                    if (_currentExtentIndex != 0)
                    {
                        _currentExtentIndex--;

                        if (_currentExtentIndex == 0)
                        {
                            _previousExtentImage.Opacity = 0.3;
                            _previousExtentImage.IsHitTestVisible = false;
                        }

                        _newExtent = false;

                        MyMap.IsHitTestVisible = false;
                        MyMap.ZoomTo(_extentHistory[_currentExtentIndex]);

                        if (_nextExtentImage.IsHitTestVisible == false)
                        {
                            _nextExtentImage.Opacity = 1;
                            _nextExtentImage.IsHitTestVisible = true;
                        }
                    }
                    break;
                case 4: // Next Extent
                    if (_currentExtentIndex < _extentHistory.Count - 1)
                    {
                        _currentExtentIndex++;

                        if (_currentExtentIndex == (_extentHistory.Count - 1))
                        {
                            _nextExtentImage.Opacity = 0.3;
                            _nextExtentImage.IsHitTestVisible = false;
                        }

                        _newExtent = false;

                        MyMap.IsHitTestVisible = false;
                        MyMap.ZoomTo(_extentHistory[_currentExtentIndex]);

                        if (_previousExtentImage.IsHitTestVisible == false)
                        {
                            _previousExtentImage.Opacity = 1;
                            _previousExtentImage.IsHitTestVisible = true;
                        }
                    }
                    break;
                case 5: // Default Extent
                    MyMap.ZoomTo(_extentHistory[0]);
                    break;
                case 6: // Full Screen
                    Application.Current.Host.Content.IsFullScreen = !Application.Current.Host.Content.IsFullScreen;
                    break;
            }
        }

        // Get Latest HART Dataset_id from Agency table
        void HartAgencyQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;
            
            DateTime latestDt = Convert.ToDateTime(featureSet.ElementAt(0).Attributes["COLLECTED_DATE"].ToString());
            int latestIndex = 0;

            for (int i = 1; i < featureSet.Features.Count; i++)
            {
                DateTime dt = Convert.ToDateTime(featureSet.ElementAt(i).Attributes["COLLECTED_DATE"].ToString());
                if (DateTime.Compare(latestDt, dt) < 0)
                {
                    latestDt = dt;
                    latestIndex = i;
                }
            }

            //Hart
            _datasetId[0] = Convert.ToInt64(featureSet.ElementAt(latestIndex).Attributes["DATASET_ID"].ToString());
            QueryOtherTables();
        }

        // Get Latest PSTA Dataset_id from Agency table
        void PstaAgencyQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;

            DateTime latestDt = Convert.ToDateTime(featureSet.ElementAt(0).Attributes["COLLECTED_DATE"].ToString());
            int latestIndex = 0;

            for (int i = 1; i < featureSet.Features.Count; i++)
            {
                DateTime dt = Convert.ToDateTime(featureSet.ElementAt(i).Attributes["COLLECTED_DATE"].ToString());
                if (DateTime.Compare(latestDt, dt) < 0)
                {
                    latestDt = dt;
                    latestIndex = i;
                }
            }
            
            // Psta
            _datasetId[1] = Convert.ToInt64(featureSet.ElementAt(latestIndex).Attributes["DATASET_ID"].ToString());
            QueryOtherTables();
        }

        private void QueryOtherTables()
        {
            HartQueryRoutes(_datasetId[0].ToString());
            QueryTrips(_datasetId[0].ToString(), 0);

            ESRI.ArcGIS.Client.FeatureLayer hartStopFeatureLayer = MyMap.Layers["HartStopFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            hartStopFeatureLayer.Where = "DATASET_ID = " + _datasetId[0].ToString();
            hartStopFeatureLayer.Update();

            PstaQueryRoutes(_datasetId[1].ToString());
            QueryTrips(_datasetId[1].ToString(), 1);

            ESRI.ArcGIS.Client.FeatureLayer pstaStopFeatureLayer = MyMap.Layers["PstaStopFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            pstaStopFeatureLayer.Where = "DATASET_ID = " + _datasetId[1].ToString();
            pstaStopFeatureLayer.Update();
        }

        // Query HART Route Information - D7TRADM.CUTR4_ROUTES_SERVICE
        private void HartQueryRoutes(string datasetId)
        {
            QueryTask routeQueryTask =
                new QueryTask("http://dotsd7gispro.d7.dot.state.fl.us/arcgis/rest/services/Public_View/MapServer/5");
            routeQueryTask.ExecuteCompleted += HartRouteQueryTask_ExecuteCompleted;
            routeQueryTask.Failed += QueryTask_Failed;

            ESRI.ArcGIS.Client.Tasks.Query routeQuery = new ESRI.ArcGIS.Client.Tasks.Query();
            routeQuery.Where = "DATASET_ID = " + datasetId;
            routeQuery.OutFields.Add("*");
            routeQuery.ReturnGeometry = false;
            routeQueryTask.ExecuteAsync(routeQuery);
        }

        // Add HART Route information to the corresponding Shape
        void HartRouteQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;

            for (int i = 0; i < featureSet.Features.Count; i++)
            {
                string rId = featureSet.ElementAt(i).Attributes["ROUTE_ID"].ToString();
                string rShortName = featureSet.ElementAt(i).Attributes["ROUTE_SHORT_NAME"].ToString();
                string rLongName = featureSet.ElementAt(i).Attributes["ROUTE_LONG_NAME"].ToString();
                string rType = featureSet.ElementAt(i).Attributes["ROUTE_TYPE"].ToString();
                Route r = new Route(rId, rShortName, rLongName, rType);
                if (featureSet.ElementAt(i).Attributes["ROUTE_SUB_TYPE"] != null)
                {
                    r.RSubType = featureSet.ElementAt(i).Attributes["ROUTE_SUB_TYPE"].ToString();
                }
                
                try
                {
                    hartRouteIdToRoute.Add(rId, r);
                }
                catch (Exception e)
                {
                    //MessageBox.Show(e.Message+" "+rId);
                }
            }
        }

        // Query PSTA Route Information - D7TRADM.CUTR4_ROUTES_SERVICE
        private void PstaQueryRoutes(string datasetId)
        {
            QueryTask routeQueryTask =
                new QueryTask("http://dotsd7gispro.d7.dot.state.fl.us/arcgis/rest/services/Public_View/MapServer/5");
            routeQueryTask.ExecuteCompleted += PstaRouteQueryTask_ExecuteCompleted;
            routeQueryTask.Failed += QueryTask_Failed;

            ESRI.ArcGIS.Client.Tasks.Query routeQuery = new ESRI.ArcGIS.Client.Tasks.Query();
            routeQuery.Where = "DATASET_ID = " + datasetId;
            routeQuery.OutFields.Add("*");
            routeQuery.ReturnGeometry = false;
            routeQueryTask.ExecuteAsync(routeQuery);
        }

        // Add PSTA Route information to the corresponding Shape
        void PstaRouteQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;

            for (int i = 0; i < featureSet.Features.Count; i++)
            {
                string rId = featureSet.ElementAt(i).Attributes["ROUTE_ID"].ToString();
                string rShortName = featureSet.ElementAt(i).Attributes["ROUTE_SHORT_NAME"].ToString();
                string rLongName = featureSet.ElementAt(i).Attributes["ROUTE_LONG_NAME"].ToString();
                string rType = featureSet.ElementAt(i).Attributes["ROUTE_TYPE"].ToString();
                Route r = new Route(rId, rShortName, rLongName, rType);
                if (featureSet.ElementAt(i).Attributes["ROUTE_SUB_TYPE"] != null)
                {
                    r.RSubType = featureSet.ElementAt(i).Attributes["ROUTE_SUB_TYPE"].ToString();
                }

                try
                {
                    pstaRouteIdToRoute.Add(rId, r);
                }
                catch (Exception e)
                {
                    //MessageBox.Show(e.Message+" "+rId);
                }
            }
        }

        // Query Trip table - D7TRADM.CUTR4_TRIPS_SERVICE
        private void QueryTrips(string datasetId, int agencyIndex)
        {
            QueryTask tripQueryTask =
                new QueryTask("http://dotsd7gispro.d7.dot.state.fl.us/arcgis/rest/services/Public_View/MapServer/1");
            if (agencyIndex == 0)
            {
                tripQueryTask.ExecuteCompleted += HartTripQueryTask_ExecuteCompleted;
            }
            else
            {
                tripQueryTask.ExecuteCompleted += PstaTripQueryTask_ExecuteCompleted;
            }
            tripQueryTask.Failed += QueryTask_Failed;

            ESRI.ArcGIS.Client.Tasks.Query tripQuery = new ESRI.ArcGIS.Client.Tasks.Query();
            tripQuery.Where = "DATASET_ID = "+datasetId;
            tripQuery.OutFields.Add("*");
            tripQuery.ReturnGeometry = false;
            tripQueryTask.ExecuteAsync(tripQuery);
        }

        // Matching HART shapeId and routeId
        void HartTripQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;
            
            HashSet<string> shapeIds = new HashSet<string>();
            for (int i = 0; i < featureSet.Count(); i++)
            {
                try
                {
                    string shapeId = featureSet.ElementAt(i).Attributes["SHAPE_ID"].ToString();
                    if (shapeIds.Contains(shapeId))
                    {
                        continue;
                    }
                    string routeId = featureSet.ElementAt(i).Attributes["ROUTE_ID"].ToString();
                    hartShapeIdToRoute.Add(shapeId, hartRouteIdToRoute[routeId]);
                    shapeIds.Add(shapeId);
                }
                catch (Exception e)
                {
                    //MessageBox.Show(e.Message);
                }
            }

            ESRI.ArcGIS.Client.FeatureLayer hartShapeFeatureLayer = MyMap.Layers["HartShapeFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            hartShapeFeatureLayer.Where = "DATASET_ID = " + _datasetId[0].ToString();
            hartShapeFeatureLayer.Update();
        }

        // Matching PSTA shapeId and routeId
        void PstaTripQueryTask_ExecuteCompleted(object sender, ESRI.ArcGIS.Client.Tasks.QueryEventArgs args)
        {
            FeatureSet featureSet = args.FeatureSet;

            HashSet<string> shapeIds = new HashSet<string>();
            for (int i = 0; i < featureSet.Features.Count; i++)
            {
                try
                {
                    string shapeId = featureSet.ElementAt(i).Attributes["SHAPE_ID"].ToString();
                    if (shapeIds.Contains(shapeId))
                    {
                        continue;
                    }
                    string routeId = featureSet.ElementAt(i).Attributes["ROUTE_ID"].ToString();
                    pstaShapeIdToRoute.Add(shapeId, pstaRouteIdToRoute[routeId]);
                    shapeIds.Add(shapeId);
                }
                catch (Exception e)
                {
                    //MessageBox.Show(e.Message);
                }
            }

            ESRI.ArcGIS.Client.FeatureLayer pstaShapeFeatureLayer = MyMap.Layers["PstaShapeFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            pstaShapeFeatureLayer.Where = "DATASET_ID = " + _datasetId[1].ToString();
            pstaShapeFeatureLayer.Update();
        }

        private void QueryTask_Failed(object sender, TaskFailedEventArgs args)
        {
            MessageBox.Show("Query execute error: " + args.Error);
        }

        // Remove the Base Layer on the list
        private void MyList_Loaded(object sender, RoutedEventArgs e)
        {
            LayerCollection tempLayers = new LayerCollection();
            foreach (Layer l in MyMap.Layers)
            {
                tempLayers.Add(l);
            }
            tempLayers.RemoveAt(0);
            MyList.ItemsSource = tempLayers;
        }

        // Add HART Route information to corresponding Shape for display purpose
        private void HartShapeFeatureLayer_UpdateCompleted(object sender, EventArgs e)
        {
            ESRI.ArcGIS.Client.FeatureLayer hartShapeFeatureLayer = MyMap.Layers["HartShapeFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            GraphicCollection gc = hartShapeFeatureLayer.Graphics;

            foreach (Graphic g in gc)
            {
                string sid = g.Attributes["SHAPE_ID"].ToString();

                try
                {
                    g.Attributes.Add("ROUTE_ID", hartShapeIdToRoute[sid].RId);
                    g.Attributes.Add("ROUTE_SHORT_NAME", hartShapeIdToRoute[sid].RShortName);
                    g.Attributes.Add("ROUTE_LONG_NAME", hartShapeIdToRoute[sid].RLongName);
                    g.Attributes.Add("ROUTE_TYPE", hartShapeIdToRoute[sid].RType);
                    g.Attributes.Add("ROUTE_SUB_TYPE", hartShapeIdToRoute[sid].RSubType);
                }
                catch (Exception ex)
                {
                    string mess = ex.Message;
                }
            }
        }

        // Add PSTA Route information to corresponding Shape for display purpose
        private void PstaShapeFeatureLayer_UpdateCompleted(object sender, EventArgs e)
        {
            ESRI.ArcGIS.Client.FeatureLayer pstaShapeFeatureLayer = MyMap.Layers["PstaShapeFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            GraphicCollection gc = pstaShapeFeatureLayer.Graphics;
            foreach (Graphic g in gc)
            {
                string sid = g.Attributes["SHAPE_ID"].ToString();
                
                try
                {
                    g.Attributes.Add("ROUTE_ID", pstaShapeIdToRoute[sid].RId);
                    g.Attributes.Add("ROUTE_SHORT_NAME", pstaShapeIdToRoute[sid].RShortName);
                    g.Attributes.Add("ROUTE_LONG_NAME", pstaShapeIdToRoute[sid].RLongName);
                    g.Attributes.Add("ROUTE_TYPE", pstaShapeIdToRoute[sid].RType);
                    g.Attributes.Add("ROUTE_SUB_TYPE", pstaShapeIdToRoute[sid].RSubType);
                }
                catch (Exception ex)
                {
                    string mess = ex.Message;
                }
            }
        }

        // Store original Shape data
        private void StoreOriginalData(GraphicCollection hartData, GraphicCollection pstaData)
        {
            hartOriginalShapes = new GraphicCollection(hartData);
            pstaOriginalShapes = new GraphicCollection(pstaData);
            isShapeModifiedQuery = true;
        }

        // Filter Shape by ROUTE_SUB_TYPE
        private void FilterShapeByRouteSubType(string value)
        {
            ESRI.ArcGIS.Client.FeatureLayer hartShapeFeatureLayer = MyMap.Layers["HartShapeFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            ESRI.ArcGIS.Client.FeatureLayer pstaShapeFeatureLayer = MyMap.Layers["PstaShapeFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;

            if (!isShapeModifiedQuery) StoreOriginalData(hartShapeFeatureLayer.Graphics, pstaShapeFeatureLayer.Graphics);
            //hartShapeFeatureLayer.Graphics
            GraphicCollection gcHartNew = new GraphicCollection(hartOriginalShapes);
            hartShapeFeatureLayer.ClearGraphics();
            foreach (Graphic g in hartOriginalShapes)
            {
                if (value == null | value.Equals("")) 
                {
                    hartShapeFeatureLayer.Graphics.Add(g);
                }
                else 
                {
                    if (g.Attributes["ROUTE_SUB_TYPE"] != null)
                    {
                        string subType = g.Attributes["ROUTE_SUB_TYPE"].ToString();
                        if (!subType.Equals(value))
                        {
                            hartShapeFeatureLayer.Graphics.Remove(g);
                        }
                        else
                        {
                            hartShapeFeatureLayer.Graphics.Add(g);
                        }
                    }
                    else
                    {
                        hartShapeFeatureLayer.Graphics.Remove(g);
                    }
                }
            }

            hartShapeFeatureLayer.Refresh();
            //hartShapeFeatureLayer.Update();

            GraphicCollection gcPstaNew = new GraphicCollection(pstaOriginalShapes);
            pstaShapeFeatureLayer.ClearGraphics();
            foreach (Graphic g in pstaOriginalShapes)
            {
                if (value == null | value.Equals(""))
                {
                    pstaShapeFeatureLayer.Graphics.Add(g);
                }
                else
                {
                    if (g.Attributes["ROUTE_SUB_TYPE"] != null)
                    {
                        string subType = g.Attributes["ROUTE_SUB_TYPE"].ToString();
                        if (!subType.Equals(value))
                        {
                            pstaShapeFeatureLayer.Graphics.Remove(g);
                        }
                        else
                        {
                            pstaShapeFeatureLayer.Graphics.Add(g);
                        }
                    }
                    else
                    {
                        pstaShapeFeatureLayer.Graphics.Remove(g);
                    }
                }
            }

            pstaShapeFeatureLayer.Refresh();
            //pstaShapeFeatureLayer.Graphics = gcPstaNew;
        }

        // Filter Stop by where clause
        private void FilterStopByWhereClause(string clause)
        {
            ESRI.ArcGIS.Client.FeatureLayer hartStopFeatureLayer = MyMap.Layers["HartStopFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;
            ESRI.ArcGIS.Client.FeatureLayer pstaStopFeatureLayer = MyMap.Layers["PstaStopFeatureLayer"] as ESRI.ArcGIS.Client.FeatureLayer;

            if (clause == null | clause.Equals(""))
            {
                hartStopFeatureLayer.Where = "DATASET_ID = " + _datasetId[0].ToString();
                pstaStopFeatureLayer.Where = "DATASET_ID = " + _datasetId[1].ToString();
            }
            else
            {
                hartStopFeatureLayer.Where = clause + " AND DATASET_ID = " + _datasetId[0].ToString();
                pstaStopFeatureLayer.Where = clause + " AND DATASET_ID = " + _datasetId[1].ToString();
            }

            hartStopFeatureLayer.Update();
            pstaStopFeatureLayer.Update();
        }

        // Find Button Click Event
        private void ExecuteButton_Click(object sender, RoutedEventArgs e)
        {
            int selectedIndex = FindAttrTable.SelectedIndex;
            if (selectedIndex == 0)
            {
                FilterStopByWhereClause(FindAttrValue.Text);
            }
            else if (selectedIndex == 1)
            {
                FilterShapeByRouteSubType(FindAttrValue.Text);
            }
        }

        private void FindTask_Failed(object sender, TaskFailedEventArgs args)
        {
            MessageBox.Show("Find failed: " + args.Error);
        }

        // Change the text corresponding to tables
        private void FindAttrTable_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (FindAttrTable == null) return;

            if (FindAttrTable.SelectedIndex == 0)
            {
                whereTextLabel.Text = "Where";
            }
            else if (FindAttrTable.SelectedIndex == 1)
            {
                whereTextLabel.Text = "Where ROUTE_SUB_TYPE = ";
            }
        }

    }
}