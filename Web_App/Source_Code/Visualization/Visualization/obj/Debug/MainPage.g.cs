﻿#pragma checksum "C:\Users\Agsuser\Desktop\FDOT7 on-site working\Visualization\Visualization\MainPage.xaml" "{406ea660-64cf-4c82-b6f0-42d48172a799}" "262C7AAAC40B953A9009FCE0BAEDCE90"
//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.261
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System;
using System.Windows;
using System.Windows.Automation;
using System.Windows.Automation.Peers;
using System.Windows.Automation.Provider;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Interop;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Windows.Resources;
using System.Windows.Shapes;
using System.Windows.Threading;


namespace Visualization {
    
    
    public partial class MainPage : System.Windows.Controls.UserControl {
        
        internal System.Windows.Controls.Grid LayoutRoot;
        
        internal ESRI.ArcGIS.Client.Map MyMap;
        
        internal ESRI.ArcGIS.Client.ArcGISTiledMapServiceLayer BaseLayer;
        
        internal ESRI.ArcGIS.Client.FeatureLayer WorkProgramFeatureLayer;
        
        internal ESRI.ArcGIS.Client.FeatureLayer HartShapeFeatureLayer;
        
        internal ESRI.ArcGIS.Client.FeatureLayer PstaShapeFeatureLayer;
        
        internal ESRI.ArcGIS.Client.FeatureLayer HartStopFeatureLayer;
        
        internal ESRI.ArcGIS.Client.FeatureLayer PstaStopFeatureLayer;
        
        internal System.Windows.Controls.ListBox MyList;
        
        internal System.Windows.Controls.ComboBox FindAttrTable;
        
        internal System.Windows.Controls.ComboBoxItem stops;
        
        internal System.Windows.Controls.ComboBoxItem routes;
        
        internal System.Windows.Controls.TextBlock whereTextLabel;
        
        internal System.Windows.Controls.TextBox FindAttrValue;
        
        internal System.Windows.Controls.Button ExecuteButton;
        
        internal System.Windows.Controls.DomainDataSource domainDataSource1;
        
        internal System.Windows.Controls.DomainDataSource domainDataSource2;
        
        internal System.Windows.Controls.DataVisualization.Charting.PieSeries pieSeries1;
        
        internal ESRI.ArcGIS.Client.Toolkit.Toolbar MyToolbar;
        
        internal System.Windows.Controls.TextBlock StatusTextBlock;
        
        internal ESRI.ArcGIS.Client.Toolkit.MapProgressBar MyProgressBar;
        
        private bool _contentLoaded;
        
        /// <summary>
        /// InitializeComponent
        /// </summary>
        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        public void InitializeComponent() {
            if (_contentLoaded) {
                return;
            }
            _contentLoaded = true;
            System.Windows.Application.LoadComponent(this, new System.Uri("/Visualization;component/MainPage.xaml", System.UriKind.Relative));
            this.LayoutRoot = ((System.Windows.Controls.Grid)(this.FindName("LayoutRoot")));
            this.MyMap = ((ESRI.ArcGIS.Client.Map)(this.FindName("MyMap")));
            this.BaseLayer = ((ESRI.ArcGIS.Client.ArcGISTiledMapServiceLayer)(this.FindName("BaseLayer")));
            this.WorkProgramFeatureLayer = ((ESRI.ArcGIS.Client.FeatureLayer)(this.FindName("WorkProgramFeatureLayer")));
            this.HartShapeFeatureLayer = ((ESRI.ArcGIS.Client.FeatureLayer)(this.FindName("HartShapeFeatureLayer")));
            this.PstaShapeFeatureLayer = ((ESRI.ArcGIS.Client.FeatureLayer)(this.FindName("PstaShapeFeatureLayer")));
            this.HartStopFeatureLayer = ((ESRI.ArcGIS.Client.FeatureLayer)(this.FindName("HartStopFeatureLayer")));
            this.PstaStopFeatureLayer = ((ESRI.ArcGIS.Client.FeatureLayer)(this.FindName("PstaStopFeatureLayer")));
            this.MyList = ((System.Windows.Controls.ListBox)(this.FindName("MyList")));
            this.FindAttrTable = ((System.Windows.Controls.ComboBox)(this.FindName("FindAttrTable")));
            this.stops = ((System.Windows.Controls.ComboBoxItem)(this.FindName("stops")));
            this.routes = ((System.Windows.Controls.ComboBoxItem)(this.FindName("routes")));
            this.whereTextLabel = ((System.Windows.Controls.TextBlock)(this.FindName("whereTextLabel")));
            this.FindAttrValue = ((System.Windows.Controls.TextBox)(this.FindName("FindAttrValue")));
            this.ExecuteButton = ((System.Windows.Controls.Button)(this.FindName("ExecuteButton")));
            this.domainDataSource1 = ((System.Windows.Controls.DomainDataSource)(this.FindName("domainDataSource1")));
            this.domainDataSource2 = ((System.Windows.Controls.DomainDataSource)(this.FindName("domainDataSource2")));
            this.pieSeries1 = ((System.Windows.Controls.DataVisualization.Charting.PieSeries)(this.FindName("pieSeries1")));
            this.MyToolbar = ((ESRI.ArcGIS.Client.Toolkit.Toolbar)(this.FindName("MyToolbar")));
            this.StatusTextBlock = ((System.Windows.Controls.TextBlock)(this.FindName("StatusTextBlock")));
            this.MyProgressBar = ((ESRI.ArcGIS.Client.Toolkit.MapProgressBar)(this.FindName("MyProgressBar")));
        }
    }
}

