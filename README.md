[![GitHub release](https://img.shields.io/github/release/sismics/play-stripe.svg?style=flat-square)](https://github.com/sismics/play-stripe/releases/latest)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# play-stripe plugin

This plugin adds [Stripe](https://stripe.com) support to Play! Framework 1 applications.

# Features

# How to use

####  Add the dependency to your `dependencies.yml` file

```
require:
    - stripe -> stripe 1.3.0

repositories:
    - sismicsNexusRaw:
        type: http
        artifact: "https://nexus.sismics.com/repository/sismics/[module]-[revision].zip"
        contains:
            - stripe -> *

```
####  Set configuration parameters

Add the following parameters to **application.conf**:

```
# Stripe configuration
# ~~~~~~~~~~~~~~~~~~~~
stripe.mock=false
stripe.clientId=12345678
stripe.webhook.secret_key=12345678
```
####  Use the API

```
SubscriptionService subscriptionService = StripeApi.get().getSubscriptionService();
SubscriptionData subscriptionData = subscriptionService.getSubscription("1234");
```

####  Mock the Stripe server in dev

We recommand to mock Stripe in development mode and test profile.

Use the following configuration parameter:

```
stripe.mock=true
```

# License

This software is released under the terms of the Apache License, Version 2.0. See `LICENSE` for more
information or see <https://opensource.org/licenses/Apache-2.0>.
