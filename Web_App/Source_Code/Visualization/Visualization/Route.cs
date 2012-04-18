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
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace Visualization
{
    public class Route
    {
        private string rId, rShortName, rLongName, rType, rSubType;

        public string RSubType
        {
            get { return rSubType; }
            set { rSubType = value; }
        }

        public string RId
        {
            get { return rId; }
            set { rId = value; }
        }

        public string RShortName
        {
            get { return rShortName; }
            set { rShortName = value; }
        }

        public string RLongName
        {
            get { return rLongName; }
            set { rLongName = value; }
        }

        public string RType
        {
            get { return rType; }
            set { rType = value; }
        }

        public Route(string rId, string rShortName, string rLongName, string rType)
        {
            RId = rId;
            RShortName = rShortName;
            RLongName = rLongName;
            RType = rType;
        }
    }
}
