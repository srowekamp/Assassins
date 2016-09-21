//
//  ViewController.swift
//  Whats My Position - GPS Testing
//
//  Created by Scott Rowekamp on 9/21/16.
//  Copyright © 2016 dev-scott. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class ViewController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {

    @IBOutlet weak var mapData: MKMapView!

    @IBOutlet weak var locationLabel: UILabel!
    
    @IBAction func getLocation(_ sender: AnyObject) {
        print(mapData.userLocation.location)
        locationLabel.text = "Your latitude is: \((locMan.location?.coordinate.latitude)!)"
    }
    
    let locMan = CLLocationManager();
    
    override func viewDidLoad() {
        
        
        
        super.viewDidLoad()
        
        self.locMan.delegate = self
        self.locMan.desiredAccuracy = kCLLocationAccuracyBest
        self.locMan.requestWhenInUseAuthorization()
        self.locMan.startUpdatingLocation()
        
        
        mapData.delegate = self
        mapData.userTrackingMode = MKUserTrackingMode(rawValue: 1)!
        mapData.showsUserLocation = true
        locationLabel.text = "Location: Unknown"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Location Delegate Methods
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last
        let center = CLLocationCoordinate2DMake((location?.coordinate.latitude)!, (location?.coordinate.longitude)!)
        let region = MKCoordinateRegion(center: center, span: MKCoordinateSpanMake(1, 1))
        mapData.setRegion(region, animated: true)
        locMan.stopUpdatingLocation()
        
    }


}

