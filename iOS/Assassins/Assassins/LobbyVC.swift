//
//  LobbyVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class LobbyVC: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    
    let locationManager = CLLocationManager();
    
    private var _playArea: MKCircle = MKCircle()
    var playArea:MKCircle {
        get {
            return _playArea
        }
        set {
            mapView.remove(_playArea)
            _playArea = newValue
            mapView.add(newValue)
        }
    }

    @IBOutlet weak var mapView: MKMapView!
    
    @IBAction func goBack(_ sender: AnyObject) {
        self.dismiss(animated: true, completion: nil)
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Setup Location Manager
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
        
        // Prepare Map
        mapView.delegate = self
        mapView.userTrackingMode = MKUserTrackingMode(rawValue: 1)!
        mapView.showsUserLocation = true
        mapView.mapType = MKMapType.hybrid
    }
    
    override func viewDidAppear(_ animated: Bool) {
        locationManager.startUpdatingLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last
        let center = CLLocationCoordinate2DMake((location?.coordinate.latitude)!, (location?.coordinate.longitude)!)
        let region = MKCoordinateRegion(center: center, span: MKCoordinateSpanMake(0.005, 0.005))
        mapView.setRegion(region, animated: false)
        playArea = MKCircle(center: center, radius: 100 as CLLocationDistance)
        locationManager.stopUpdatingLocation()
    }
    
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        if overlay is MKCircle {
            let circle = MKCircleRenderer(overlay: overlay)
            circle.strokeColor = UIColor.red
            circle.fillColor = UIColor(red: 255, green: 0, blue: 0, alpha: 0.1)
            circle.lineWidth = 1
            return circle
        } else {
            return MKOverlayRenderer(overlay: overlay)
        }
    }
}
