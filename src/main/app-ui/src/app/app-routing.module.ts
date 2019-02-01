import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


import { AppComponent } from './app.component';
import { HomePageComponent } from './home-page/home-page.component';
import { AnalysisPageComponent } from './analysis-page/analysis-page.component';
import { MethodsPageComponent } from './methods-page/methods-page.component';
import { ContactPageComponent } from './contact-page/contact-page.component';
import { FaqPageComponent } from './faq-page/faq-page.component';
import { GlossaryPageComponent } from './glossary-page/glossary-page.component';
import { PublicationsPageComponent } from './publications-page/publications-page.component';
import { PrivacyPageComponent } from './privacy-page/privacy-page.component';
import { LearnMorePageComponent } from './learn-more-page/learn-more-page.component';
import { ProjectPageComponent } from './project-page/project-page.component';
import { PhenotypeofthemonthPageComponent } from './phenotypeofthemonth-page/phenotypeofthemonth-page.component';
import {ImageacknowledgementsPageComponent} from './imageacknowledgements-page/imageacknowledgements-page.component';
import { DetailsPageComponent } from './details-page/details-page.component';
import { StrengthNLimitationsComponent } from './strength-n-limitations/strength-n-limitations.component';
import { AboutComponent } from './about/about.component';
import { ConsortiumComponent } from './consortium/consortium.component';
import { StatisticalDesignComponent } from './statistical-design/statistical-design.component';
import { FlowFilesComponent } from './flow-files/flow-files.component';
import { DataDownloadsComponent } from './data-downloads/data-downloads.component';
import { MapOfImmuneComponent } from './map-of-immune/map-of-immune.component';


const appRoutes: Routes = [
  {
    path: 'home',
    component: HomePageComponent,
  },
  {
    path: 'more_data',
    component: AnalysisPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'methods',
    component: MethodsPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'contact',
    component: ContactPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'new_cell_types',
    component: FaqPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'glossary',
    component: GlossaryPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'publications',
    component: PublicationsPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'privacy',
    component: PrivacyPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'project',
    component: ProjectPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'phenotypeofthemonth',
    component: PhenotypeofthemonthPageComponent,
    // outlet: 'popup'
  },

  {
    path: 'image-acknowledgements',
    component: ImageacknowledgementsPageComponent,
    // outlet: 'popup'
  },

  {
    path: 'whats_new',
    component: LearnMorePageComponent,
    // outlet: 'popup'
  },

  {
    path: 'details',
    component: DetailsPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'strengths',
    component: StrengthNLimitationsComponent,
    // outlet: 'popup'
  },
  {
    path: 'about',
    component: AboutComponent,
    // outlet: 'popup'
  },
  {
    path: 'consortium',
    component: ConsortiumComponent,
    // outlet: 'popup'
  },
  {
    path: 'statistical',
    component: StatisticalDesignComponent,
    // outlet: 'popup'
  },
  {
    path: 'flow',
    component: FlowFilesComponent,
    // outlet: 'popup'
  },
  {
    path: 'downloads',
    component: DataDownloadsComponent,
    // outlet: 'popup'
  },
  {
    path: 'map',
    component: MapOfImmuneComponent,
    // outlet: 'popup'
  },


   { path: '',   redirectTo: 'home', pathMatch: 'full' }
//   { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      appRoutes,
      {
        enableTracing: false, // <-- debugging purposes only
        anchorScrolling: 'enabled'

      }
    )
  ],
  exports: [
    RouterModule
  ],
  providers: [

  ]
})
export class AppRoutingModule { }

