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

class ViewController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var addressTable: UITableView!
    @IBOutlet weak var mapData: MKMapView!
    
    let geoCoder = CLGeocoder();
    let locMan = CLLocationManager();
    
    var location_data = [String?]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Setup Location Manager
        self.locMan.delegate = self
        self.locMan.desiredAccuracy = kCLLocationAccuracyBest
        self.locMan.requestWhenInUseAuthorization()
        self.locMan.startUpdatingLocation()
        
        // Prepare Map
        mapData.delegate = self
        mapData.userTrackingMode = MKUserTrackingMode(rawValue: 1)!
        mapData.showsUserLocation = true
        
        addressTable.dataSource = self
        addressTable.delegate = self
        
        
        // Grab Location and Update Table
        getAddress()
        
        
        
    }
    
    // MARK: - Table View Delegate Functions
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return (location_data.count)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        print("mark1")
        let cell = UITableViewCell(style: UITableViewCellStyle.default, reuseIdentifier: "keyValueCell")
        
        cell.textLabel!.text = location_data[indexPath.row]
        
        return cell
    }
    
    // MARK: - Location Functions
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last
        let center = CLLocationCoordinate2DMake((location?.coordinate.latitude)!, (location?.coordinate.longitude)!)
        let region = MKCoordinateRegion(center: center, span: MKCoordinateSpanMake(1, 1))
        mapData.setRegion(region, animated: false)
        locMan.stopUpdatingLocation()
        
    }

    func getAddress(){
        geoCoder.reverseGeocodeLocation(locMan.location!, completionHandler: {(placemarks, error) -> Void in
            if error == nil {
                if (placemarks?.count)! > 0 {
                    let pm = placemarks![0]
                    // Store Data Here
                    var i = 0
                    for (key, value) in pm.addressDictionary! {
                        self.location_data.append("\(key): \(value)")
                        i += 1
                    }
                    print("should reload data")
                    self.addressTable.reloadData()
                }
            }
            return
        })
    }
}

