import { Component,  AfterViewInit} from '@angular/core';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';


@Component({
  selector: 'threei-project-page',
  templateUrl: './project-page.component.html',
  styleUrls: ['./project-page.component.css']
})
export class ProjectPageComponent  {


  constructor( private router: Router ) {
    router.events.subscribe(event => {
        if (event instanceof NavigationEnd) {
          console.log('navigation end');
            const tree = router.parseUrl(router.url);
            if (tree.fragment) {
              console.log('is a fragment=' + tree.fragment);
                const element = document.getElementById(tree.fragment.valueOf());
                console.log('element found=' + element.getAttribute('name'));
                if (element) {
                    element.scrollIntoView(true);
                }
            }
         }
    });
}

}
