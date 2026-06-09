// This program finds the inscription of a circle

#include<boost/shared_ptr.hpp>

#include<CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include<CGAL/Polygon_2.h>
#include<CGAL/create_straight_skeleton_2.h>

#include <CGAL/Cartesian.h>
#include <CGAL/Exact_rational.h>
#include <CGAL/Arr_circle_segment_traits_2.h>
#include <CGAL/Arrangement_2.h>

#include "print.h"
#include <vector>
#include <string>
#include <fstream>
#include <boost/algorithm/string.hpp>
#include <chrono>
using namespace std::chrono;
using namespace std;

const string FILE_NAME = "../data/room.txt";

typedef CGAL::Exact_predicates_inexact_constructions_kernel K;

typedef K::Point_2                   Point;
typedef CGAL::Polygon_2<K>           Polygon_2;
typedef CGAL::Straight_skeleton_2<K> Ss;

typedef boost::shared_ptr<Ss> SsPtr;
using std::vector;

typedef CGAL::Cartesian<CGAL::Exact_rational>         Kernel;
typedef Kernel::Circle_2                              Circle_2;
typedef Kernel::Segment_2                             Segment_2;
typedef CGAL::Arr_circle_segment_traits_2<Kernel>     Traits_2;
typedef Traits_2::CoordNT                             CoordNT;
typedef Traits_2::Point_2                             Point_2;
typedef Traits_2::Curve_2                             Curve_2;
typedef CGAL::Arrangement_2<Traits_2>                 Arrangement_2;

struct vertex {  
    double x;
    double y;
};

struct circle {
    Kernel::Point_2 center;
    double radius = 0;
};

struct line {
    Kernel::Point_2 p1;
    Kernel::Point_2 p2;
    Segment_2 seg;
    unsigned num_secants = 0;
    unsigned num_tang_noInt = 0;
};

void print_original_polygon(SsPtr iss)
{
    cout<< "Vertices of polygon" << endl;
    Ss::Halfedge_const_handle begin = iss->faces_begin()->halfedge()->opposite();
    Ss::Halfedge_const_handle edge = begin;
    do {
        cout<< edge->vertex()->point() << endl;
        // Iterate in the opposite direction
        edge = edge->prev();
    } while (edge != begin);
}

void print_array(vector<Point> array)
{
    cout<< "Internal vertices of skeleton" << endl;
    for (unsigned i = 0; i < array.size(); i++)
        cout<< array[i] << endl;
    
}

vector<vertex> read_file(string file_name)
{
    vector<vertex> array;
    fstream file;

    file.open(file_name, ios::in);

    if (file.is_open()) {

        string tp;
        while (getline(file, tp)) {

            vertex p;
            vector<string> results;
            boost::split(results, tp, [](char c) {return c == ','; });
            p.x = stod(results[0]);
            p.y = stod(results[1]);
            array.push_back(p);

        }
        file.close();
    }
    else {
        cerr << file_name << " File cannot be found" << endl;
    }
    return array;
}


Polygon_2 get_polygon(vector<vertex> points)
{
    Polygon_2 polygon;

    for (unsigned i = 0; i < points.size(); i++)
        polygon.push_back(Point(points[i].x, points[i].y));

    return polygon;
}

vector<line> get_lines(vector<vertex> points)
{
    vector<line> lines;

    for (unsigned i = 0; i < points.size(); i++)
    {
        line l;
        int aux = i + 1;
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

SsPtr get_skeleton(Polygon_2 polygon)
{
    SsPtr iss = CGAL::create_interior_straight_skeleton_2(polygon.vertices_begin(), polygon.vertices_end());
    // print_straight_skeleton(*iss);
  
    return iss;
}

vector<Point> get_joints_of_skeleton(SsPtr iss)
{
    vector<Point> array;
    Ss::Halfedge_iterator vi;
   
    for (vi = iss->halfedges_begin(); vi != iss->halfedges_end(); ++vi) {
        
        if (vi->vertex()->is_skeleton()) {
    
            if (array.empty())
                array.push_back(vi->vertex()->point());
            else
            {
                bool flag = true;
                unsigned i = 0;
                while (i < array.size() && flag )
                {
                    if (array[i] == vi->vertex()->point())
                        flag = false;
                    i++;
                }

                if (flag)
                    array.push_back(vi->vertex()->point());

            }
        }
    }
   
    //print_array(array);

    return array;
}

vector<circle> get_circles(vector<Point> centers, double radius)
{
    vector<circle> circles;
    circle c;

    for (unsigned i = 0; i < centers.size(); i++)
    {
        c.center = Kernel::Point_2(centers[i].x(), centers[i].y());
        c.radius = radius;
        circles.push_back(c);
    }

    return circles;
}

bool avoid_intersection(circle& c, line& l) 
{
    std::list<Curve_2>  curves;

    Circle_2 circ1 = Circle_2(c.center, CGAL::Exact_rational(c.radius));
    curves.push_back(Curve_2(circ1));

    curves.push_back(Curve_2(l.seg));

    Arrangement_2 arr;
    insert(arr, curves.begin(), curves.end());

    Arrangement_2::Vertex_const_iterator vit;
    Arrangement_2::Vertex_const_handle v_max;

    std::size_t max_degree = 0;
    unsigned num_points = 0;
    for (vit = arr.vertices_begin(); vit != arr.vertices_end(); ++vit) {
     
        num_points++;
        if (vit->degree() > max_degree) {
            v_max = vit;
            max_degree = vit->degree();
        }
    }

    if (squared_distance(c.center, l.p1).to_double() < c.radius || squared_distance(c.center, l.p2).to_double() < c.radius)
    {
        
        l.num_secants++;
    }
    else {
        if (num_points >= 6 && max_degree == 4)
        {   
            l.num_secants++;

        }
        else if (num_points <= 5 && max_degree <= 4)
        {  
            l.num_tang_noInt++;
        }
    }

    bool result = l.num_secants == 0;

    l.num_secants = 0;
    l.num_tang_noInt = 0;

    return result;
}

bool has_circle_inscription(vector<vertex> points, double diameter)
{
    Polygon_2 polygon = get_polygon(points);
    vector<line> lines = get_lines(points);
    SsPtr iss = get_skeleton(polygon);

    //print_original_polygon(iss);

    vector<Point> centers = get_joints_of_skeleton(iss);
    vector<circle> circles = get_circles(centers, diameter/2);

    bool valid;
    for (unsigned ci = 0; ci < circles.size(); ci++)
    {
        valid = true;
        unsigned j = 0;
       
        while (j < lines.size() && valid)
        {
            valid= avoid_intersection(circles[ci], lines[j]);
            j++;
        }

        if (valid)
            return true;

    }
    return false; 
}

int main()
{
    auto start = high_resolution_clock::now();

    vector<vertex> points = read_file(FILE_NAME);
    double diameter = 0.70;

    if (has_circle_inscription(points, diameter))
        cout << "Valid circle" << endl;
    else
        cout << "Invalid circle" << endl;
 
    
    auto stop = high_resolution_clock::now();
    auto duration = duration_cast<milliseconds>(stop - start);
    cout << "Duration in ms:" << duration.count() << endl;
    
   return 0;
}
