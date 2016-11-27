//
//  MapSelectVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 11/6/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class MapSelectVC: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    
    let locationManager = CLLocationManager();
    
    var mainSettingsVC:CreateGameVC!
    
    private var _center:CLLocationCoordinate2D?
    
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
    @IBOutlet weak var Slider: UISlider!
    
    // called when the slider value changes so that the view may be updated dynamicaly
    @IBAction func sliderValueChanged(_ sender: Any) {
        playArea = MKCircle(center: _center!, radius: Double(Slider.value) as CLLocationDistance)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Slider.minimumValue = 100.0
        Slider.maximumValue = 1000.0
        
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
    
    // send configured settings back to creation page
    override func viewWillDisappear(_ animated: Bool) {
        mainSettingsVC.gameRadius = Double(Slider.value)
        mainSettingsVC.xcord = Double((_center?.longitude)!)
        mainSettingsVC.ycord = Double((_center?.latitude)!)
    }
    
    // MARK: Location Manager Delegate Methods
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last
        let center = CLLocationCoordinate2DMake((location?.coordinate.latitude)!, (location?.coordinate.longitude)!)
        _center = center
        let region = MKCoordinateRegion(center: center, span: MKCoordinateSpanMake(0.005, 0.005))
        mapView.setRegion(region, animated: false)
        locationManager.stopUpdatingLocation()
    }
    
    // MARK: MapView Delegete Methods
    
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
