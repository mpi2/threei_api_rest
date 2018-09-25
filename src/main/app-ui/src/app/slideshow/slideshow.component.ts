import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'threei-slideshow',
  templateUrl: './slideshow.component.html',
  styleUrls: ['./slideshow.component.css']
})
export class SlideshowComponent implements OnInit {

  imageUrlArray = ['assets/images/slider/ANA746a.jpg', 'assets/images/slider/CD8cellattacking745.jpg', 'assets/images/slider/Diff_count_5b10e.jpg',
    'assets/images/slider/Ear_epidermis_resized_0ba5a.png', 'assets/images/slider/FACS_Plot_resized7d67.png',
    'assets/images/slider/GutL3_lowvac_0040_small0a9d.jpg', 'assets/images/slider/Influenza_in_respiratory_tract-resized_11752.jpg',
    'assets//images/slider/Macrophage_infected1639E47-resized074c.jpg', 'assets/images/slider/Neutrophil-resizedda33.jpg',
    'assets/images/slider/Salmonella-resized145f.jpg'];
    
  constructor() { }

  ngOnInit() {
  }

}
