// This program founds the internal route using offset algorithm and delimited by their access sides

#include<vector>
#include<boost/shared_ptr.hpp>
#include<CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include<CGAL/Polygon_2.h>
#include<CGAL/create_offset_polygons_2.h>
#include <vector>
#include <string>
#include <fstream>
#include <boost/algorithm/string.hpp>
#include "print.h"
#include <CGAL/Simple_cartesian.h>
#include <CGAL/centroid.h>

#include <CGAL/Cartesian.h>
#include <CGAL/Exact_rational.h>
#include <CGAL/Arr_circle_segment_traits_2.h>
#include <CGAL/Arrangement_2.h>
#include <algorithm>
#include <chrono>
using namespace std::chrono;
using namespace std;

const string PATH = "../data/";
const string FILE_NAME = "room_door_access.txt";
const vector <string> FILE_NAME_EQUIPMENT= {"bed.txt","storage.txt"};

typedef CGAL::Exact_predicates_inexact_constructions_kernel K;
typedef K::Point_2                   Point;
typedef CGAL::Polygon_2<K>           Polygon_2;
typedef CGAL::Straight_skeleton_2<K> Ss;
typedef boost::shared_ptr<Polygon_2> PolygonPtr;
typedef boost::shared_ptr<Ss> SsPtr;
typedef vector<PolygonPtr> PolygonPtrVector;


typedef CGAL::Cartesian<CGAL::Exact_rational>         Kernel;
typedef Kernel::Segment_2                             Segment_2;
typedef CGAL::Arr_circle_segment_traits_2<Kernel>     Traits_2;
typedef Traits_2::Point_2                             Point_2;
typedef Traits_2::Curve_2                             Curve_2;
typedef CGAL::Arrangement_2<Traits_2>                 Arrangement_2;
bool validAccessArea = false;

struct vertex {
    double x;
    double y;
};

struct line {
    Kernel::Point_2 p1;
    Kernel::Point_2 p2;
    Segment_2 seg;
};

struct offset_lines_min_max_xy {
    vector<vertex> min_max_xy;
    vector<line> lines;
    bool empty =true;
};

struct edge { 
    int v1;
    int v2;
    unsigned intersection= 0;
};

struct centroid_Intersection {
    Point centroid;
    vector<edge> access_side;
    vector<vertex> array;
    string idName;
    unsigned valid_acess_centroid= 0;
};

vector<vertex> read_file_polygon_and_equipment(string file_name, vector<centroid_Intersection>& centroidVector)
{
    vector<edge> array_edge;
    centroid_Intersection cetroid_data;
    vector<vertex> array;
    fstream file;

    file.open(PATH +file_name, ios::in);
    if (file.is_open()) 
    { 
        string tp;

        while (getline(file, tp)) { 
            vertex p;
            vector<string> results;
            boost::split(results, tp, [](char c) {return c == ','; });
            
            if (results[0].find('#') < results[0].length())
            {
                edge ed1;
                ed1.v1 = stod(results[1]);
                ed1.v2 = stod(results[2]);
                array_edge.push_back(ed1);
            }
            else {
                p.x = stod(results[0]);
                p.y = stod(results[1]);
                array.push_back(p);
            }

        }
        file.close(); 

        cetroid_data.access_side = array_edge;
        centroidVector.push_back(cetroid_data);
    }
    else {
        cerr << file_name << " File cannot be found" << endl;
    }
    return array;
}


void print_results(vector<centroid_Intersection>  centroids_data)
{
    unsigned aux = 0;
    for (unsigned i = 0; i < centroids_data.size(); i++)
    {
        aux = 0;
        for (unsigned ii = 0; ii < centroids_data[i].access_side.size(); ii++)
            aux += centroids_data[i].access_side[ii].intersection;

        if (centroids_data[i].access_side.size() == aux)
        {
            centroids_data[i].valid_acess_centroid++;
            cout << "Valid access to:" << centroids_data[i].idName << " equipment." << endl;
        }
        else
            cout << "Valid access to:" << centroids_data[i].idName << " equipment." << endl;
    }
}

Polygon_2 get_polygon(vector<vertex> points)
{
    Polygon_2 poly;
    for (unsigned i = 0; i < points.size(); i++)
        poly.push_back(Point(points[i].x, points[i].y));

    return poly;
}

vector<line> get_lines_offset(vector<vertex> points)
{
    vector<line> lines;

    for (unsigned i = 0; i < points.size(); i++)
    {
        line l;
        unsigned aux = i + 1;
        l.p1 = Kernel::Point_2(points[i].x, points[i].y);

        if (aux != points.size())
            l.p2 = Kernel::Point_2(points[i + 1].x, points[i + 1].y);
        else
            l.p2 = Kernel::Point_2(points[0].x, points[0].y);

        l.seg = Segment_2(l.p1, l.p2);
        lines.push_back(l);
    }

    return lines;
}

template<class K>
auto get_point_offset(CGAL::Point_2<K> const& p) 
{
    return p;
}

 template<class K, class C>
offset_lines_min_max_xy see_polygon(CGAL::Polygon_2<K, C> const& poly)
{
    typedef CGAL::Polygon_2<K, C> Polygon;

    double maxAuxX = -1000, maxAuxY = -1000, minAuxX = 1000, minAuxY = 1000;
    vector<vertex> arrayXY;
    vector<vertex> arrayVertex;

    for (typename Polygon::Vertex_const_iterator vi = poly.vertices_begin(); vi != poly.vertices_end(); ++vi)
    {
        Point p = get_point_offset(*vi);
      
        maxAuxX = max(maxAuxX, p.x());
        maxAuxY = max(maxAuxY, p.y());

        minAuxX = min(minAuxX, p.x());
        minAuxY = min(minAuxY, p.y());

        vertex p1;
        p1.x = p.x();
        p1.y = p.y();
        arrayVertex.push_back(p1);
    }
    vertex p;

    p.x = maxAuxX;
    p.y = maxAuxY;
    arrayXY.push_back(p); 

    p.x = minAuxX;
    p.y = minAuxY; 
    arrayXY.push_back(p);

    vector<line> lines = get_lines_offset(arrayVertex);

    offset_lines_min_max_xy offset;
    offset.min_max_xy = arrayXY;
    offset.lines = lines;
    offset.empty = false;

    return   offset;
}

PolygonPtrVector get_offset(Polygon_2 poly, double width)
{
    SsPtr iss = CGAL::create_interior_straight_skeleton_2(poly.vertices_begin(), poly.vertices_end());
    // print_straight_skeleton(*iss);

    PolygonPtrVector offset_polygons = CGAL::create_offset_polygons_2<Polygon_2>(width, *iss);
    // print_polygons(offset_polygons); 
    return offset_polygons;
}


template<class K, class C>
offset_lines_min_max_xy get_max_min_polygons(vector< boost::shared_ptr< CGAL::Polygon_2<K, C> > > const& polygons)
{
    typedef vector< boost::shared_ptr< CGAL::Polygon_2<K, C> > > PolygonVector;
    offset_lines_min_max_xy instance;

    if (polygons.empty())
        return instance;
    else
    {
        for (typename PolygonVector::const_iterator pi = polygons.begin(); pi != polygons.end(); ++pi)
            return see_polygon(**pi);
    }
}

vector<Point> get_2DPoints( vector<vertex> array)
{
    vector < Point> vector;
    for (unsigned i = 0; i < array.size(); i++)
        vector.push_back(Point(array[i].x, array[i].y));
    
    return vector;
}

void  get_centroid(vector<Point>  polygon, vector<centroid_Intersection> &centroidVector)
{
    centroid_Intersection cetroid_data= centroidVector.back();
    cetroid_data.centroid = CGAL::centroid(polygon.begin(), polygon.end(), CGAL::Dimension_tag<0>());

    centroidVector.back() = cetroid_data;
}

void get_equipment(vector<centroid_Intersection>& centroids_data)
{
    for (unsigned i = 0; i < FILE_NAME_EQUIPMENT.size(); i++)
    {
        vector<vertex> points = read_file_polygon_and_equipment(FILE_NAME_EQUIPMENT[i], centroids_data);

        centroid_Intersection cetroid_data_aux = centroids_data.back();
        cetroid_data_aux.array = points;
        cetroid_data_aux.idName = FILE_NAME_EQUIPMENT[i];
        centroids_data.back() = cetroid_data_aux;

        vector<Point>  polygon = get_2DPoints(points);
        get_centroid(polygon, centroids_data);

    }
}


bool has_connection(centroid_Intersection centroid_access, edge& access_side, offset_lines_min_max_xy offset)
{
    line l;
    double lambda = 5.0;

    int v1 = access_side.v1;
    int v2 = access_side.v2;
    double vecX = centroid_access.array[v1].x - centroid_access.array[v2].x;
    double vecY = centroid_access.array[v1].y - centroid_access.array[v2].y;

    double perpX = -vecY;
    double perpY = vecX;
    double rootX = perpX / (sqrt((perpX * perpX) + (perpY * perpY)));
    double rootY = perpY / (sqrt((perpX * perpX) + (perpY * perpY)));

    double pointX = lambda * rootX + centroid_access.centroid.x();
    double pointY = lambda * rootY + centroid_access.centroid.y();

    l.p1 = Kernel::Point_2(centroid_access.centroid.x(), centroid_access.centroid.y());
    l.p2 = Kernel::Point_2(pointX, pointY);
    l.seg = Segment_2(l.p1, l.p2);

    unsigned  li = 0;
    do {
        std::list<Curve_2>  curves;
        curves.push_back(Curve_2(offset.lines[li].seg));
        curves.push_back(Curve_2(l.seg));
        Arrangement_2 arr;
        insert(arr, curves.begin(), curves.end());

        Arrangement_2::Vertex_const_iterator vit;
        Arrangement_2::Vertex_const_handle v_max;
        std::size_t max_degree = 0;
        int num_points = 0;
        for (vit = arr.vertices_begin(); vit != arr.vertices_end(); ++vit) {
            num_points++;
            if (vit->degree() > max_degree) {
                v_max = vit;
                max_degree = vit->degree();
            }
        }

        if (max_degree >= 2 && num_points >= 4)
            access_side.intersection++;

        li++;
    } while (li < offset.lines.size() && access_side.intersection < 1);

    return access_side.intersection >= 1;
}


bool has_internal_route(vector<vertex> points, vector<centroid_Intersection>  centroids_data, const double width)
{
    centroid_Intersection cetroid_data_update = centroids_data.back();
    cetroid_data_update.array = points;
    cetroid_data_update.idName = FILE_NAME;
    centroids_data.back() = cetroid_data_update;
    int p1 = centroids_data[0].access_side[0].v1;
    int p2 = centroids_data[0].access_side[0].v2;

    Point centroidDoor;
    double x = (centroids_data[0].array[p1].x + centroids_data[0].array[p2].x) / 2;
    double y = (centroids_data[0].array[p1].y + centroids_data[0].array[p2].y) / 2;
    centroids_data[0].centroid = Point(x, y);

    Polygon_2 polygon = get_polygon(centroids_data[0].array);

    PolygonPtrVector offset_polygons = get_offset(polygon, width);
    offset_lines_min_max_xy offset = get_max_min_polygons(offset_polygons);

    if (offset.empty)
        return false;
    else
    {
        get_equipment(centroids_data);

        for (unsigned i = 0; i < centroids_data.size(); i++) // equipment
        {
            for (unsigned ii = 0; ii < centroids_data[i].access_side.size(); ii++) //access sides
            {
                if (!has_connection(centroids_data[i], centroids_data[i].access_side[ii], offset))
                    return false;
            }
        }

        // print_results(centroids_data);

        return true;
    }
}

int main()
{
    auto start = high_resolution_clock::now();
  
    vector<centroid_Intersection> centroids_data; 
    vector<vertex> points = read_file_polygon_and_equipment(FILE_NAME, centroids_data); 
    double width = 0.4;

    if (has_internal_route(points, centroids_data, width))
        cout << "Valid internal route" << endl;
    else
        cout << "Invalid internal route" << endl;
    
    auto stop = high_resolution_clock::now();
    auto duration = duration_cast<milliseconds>(stop - start);   
    cout << "Duration in ms:" << duration.count() << endl;

    return 0;
}
